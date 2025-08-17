package com.VishalSharma.journalApp.controller;

import com.VishalSharma.journalApp.api.response.WeatherResponse;
import com.VishalSharma.journalApp.services.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/api/weather-api")
@Tag(name = "Weather APIs", description = "Dashboard, Get Weather info")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // Health-check
    @GetMapping("/dashboard")
    @Operation(description = "WeatherController Dashboard")
    public ResponseEntity<String> dashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("GET /user/api/weather-api/dashboard invoked by user: {}", userName);

            String msg = "Welcome " + userName + "! WeatherController dashboard is working fine.";
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            log.error("Error accessing WeatherController dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong with WeatherController dashboard");
        }
    }

    // Get weather info
    @GetMapping("/{query}")
    @Operation(description = "Get Weather of a city")
    public ResponseEntity<String> getWeatherInfo(@PathVariable String query) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            log.info("GET /user/api/weather-api/{} invoked by user: {}", query, userName);

            WeatherResponse weatherForCity = weatherService.getWeatherForCity(query);

            if (weatherForCity == null || weatherForCity.getCurrent() == null) {
                log.warn("No weather data found for city: {}", query);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No matching location found with name: " + query);
            }

            double feelsLikeTempInC = weatherForCity.getCurrent().getFeelsLikeCelsius();
            double tempCelsius = weatherForCity.getCurrent().getTempCelsius();

            log.debug("Temperature for {} - Actual: {}, Feels Like: {}", query, tempCelsius, feelsLikeTempInC);

            String msg = String.format(
                    "Hi %s! The temperature in %s is %.1f°C, but it feels like %.1f°C.",
                    userName, query, tempCelsius, feelsLikeTempInC
            );

            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving weather info for city: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while fetching weather info");
        }
    }
}
