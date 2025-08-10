package com.VishalSharma.journalApp.controller;


import com.VishalSharma.journalApp.api.response.WeatherResponse;
import com.VishalSharma.journalApp.services.WeatherService;
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
public class WeatherController {


    @Autowired
    private WeatherService weatherService;

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        String msg = "Welcome " + userName + "! weatherController dashboard is working fine.";
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("{query}")
    public ResponseEntity<String> getWeatherInfo(@PathVariable String query) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            WeatherResponse weatherForCity = weatherService.getWeatherForCity(query);

            double feelsLikeTempInC = weatherForCity.getCurrent().getFeelsLikeCelsius();
            double tempCelsius = weatherForCity.getCurrent().getTempCelsius();

            String msg = "Hi " + userName + "! The temperature in " + query + " is " + tempCelsius + ", but it feels like " + feelsLikeTempInC + ".";
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("No matching location found with name: " + query, HttpStatus.NOT_FOUND);
        }
    }
}
