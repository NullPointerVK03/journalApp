package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.api.response.WeatherResponse;
import com.VishalSharma.journalApp.appCache.AppCache;
import com.VishalSharma.journalApp.constants.Placeholder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class WeatherService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;

    @Value("${weatherstack.api.key}")
    private String myApi;

    public WeatherResponse getWeatherForCity(String query) {
        try {
            log.info("Fetching weather for city: {}", query);
            WeatherResponse response = redisService.get("Weather_Of_" + query, WeatherResponse.class);
            if (response != null) {
                log.info("Weather data for {} found in Redis cache", query);
                return response;
            } else {
                log.info("Weather data for {} not found in Redis, fetching from API", query);
                String finalApi = appCache.appCache.get(AppCache.keys.WEATHER_API.toString())
                        .replace(Placeholder.API_KEY, myApi)
                        .replace(Placeholder.CITY, query);
                WeatherResponse exchanged = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class).getBody();
                redisService.set("Weather_Of_" + query, exchanged, 300L);
                log.info("Weather data for {} fetched from API and cached in Redis", query);
                return exchanged;
            }
        } catch (RestClientException e) {
            log.info("No matching location {} found.", query);
            log.error("Error occurred while fetching weather for city: {}", query, e);
            throw new RuntimeException(e);
        }
    }

    //    testing restTemplate's POST method

    //    @PostMapping("/test")
    //    public ResponseEntity<?> method() {
    //
    //        try {
    //            User user = User.builder().userName("TEST").password("123").build();
    //
    //            HttpEntity<User> httpEntity = new HttpEntity<>(user);
    //
    //
    //            String api = "localhost:8080/public/create-new-user";
    //
    //            ResponseEntity<Object> exchange = restTemplate.exchange(api, HttpMethod.POST, httpEntity, Object.class);
    //            Object body = exchange.getBody();
    //            if (body != null) {
    //                return new ResponseEntity<>(HttpStatus.CREATED);
    //            } else {
    //                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    //            }
    //        } catch (RestClientException e) {
    //            throw new RuntimeException(e);
    //        }
    //    }
}
