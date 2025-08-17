package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.api.response.WeatherResponse;
import com.VishalSharma.journalApp.appCache.AppCache;
import com.VishalSharma.journalApp.constants.Placeholder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class WeatherService {

    private static final long CACHE_TTL_SECONDS = 300L;
    private final RestTemplate restTemplate;
    private final AppCache appCache;
    private final RedisService redisService;
    @Value("${weatherstack.api.key}")
    private String myApi;

    // Constructor Injection
    public WeatherService(RestTemplate restTemplate, AppCache appCache, RedisService redisService) {
        this.restTemplate = restTemplate;
        this.appCache = appCache;
        this.redisService = redisService;
    }

    public WeatherResponse getWeatherForCity(String query) {
        String cacheKey = "Weather_Of_" + query;

        try {
            log.info("Fetching weather for city: {}", query);

            // 1. Try Redis cache first
            WeatherResponse cachedResponse = redisService.get(cacheKey, WeatherResponse.class);
            if (cachedResponse != null) {
                log.info("Weather data for '{}' served from Redis cache", query);
                return cachedResponse;
            }

            // 2. Build API URL dynamically
            String apiTemplate = appCache.appCache.get(AppCache.keys.WEATHER_API.toString());
            String finalApi = apiTemplate
                    .replace(Placeholder.API_KEY, myApi)
                    .replace(Placeholder.CITY, query);

            log.info("Fetching weather data for '{}' from external API", query);

            // 3. Call external API
            WeatherResponse apiResponse = restTemplate
                    .exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class)
                    .getBody();

            if (apiResponse == null) {
                log.warn("Weather API returned null response for '{}'", query);
                throw new RuntimeException("Weather API returned no data for " + query);
            }

            // 4. Cache the response
            redisService.set(cacheKey, apiResponse, CACHE_TTL_SECONDS);
            log.info("Weather data for '{}' cached in Redis (TTL: {} seconds)", query, CACHE_TTL_SECONDS);

            return apiResponse;

        } catch (RestClientException e) {
            log.error("Error while fetching weather for '{}': {}", query, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch weather for " + query, e);
        }
    }
}
