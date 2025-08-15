package com.VishalSharma.journalApp.controller;


import com.VishalSharma.journalApp.api.response.WeatherResponse;
import com.VishalSharma.journalApp.services.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/api/weather-api")
@Slf4j
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("Health of WeatherController dashboard is ok for user: {}", userName);
            String msg = "Welcome " + userName + "! weatherController dashboard is working fine.";
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error accessing WeatherController dashboard", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{query}")
    public ResponseEntity<String> getWeatherInfo(@PathVariable String query) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            log.info("Incoming GET request for weather info of city: {} by user: {}", query, userName);

            WeatherResponse weatherForCity = weatherService.getWeatherForCity(query);
            log.info("Weather data retrieved successfully for city: {}", query);

            double feelsLikeTempInC = weatherForCity.getCurrent().getFeelsLikeCelsius();
            double tempCelsius = weatherForCity.getCurrent().getTempCelsius();

            log.debug("Temperature for {} - Actual: {}, Feels Like: {}", query, tempCelsius, feelsLikeTempInC);

            String msg = "Hi " + userName + "! The temperature in " + query + " is " + tempCelsius + ", but it feels like " + feelsLikeTempInC + ".";
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("No matching location found for city: {}", query, e);
            return new ResponseEntity<>("No matching location found with name: " + query, HttpStatus.NOT_FOUND);
        }
    }
}
