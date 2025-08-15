package com.VishalSharma.journalApp.schedular;

import com.VishalSharma.journalApp.api.response.WeatherResponse;
import com.VishalSharma.journalApp.services.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class RedisSchedular {

    @Autowired
    private WeatherService weatherService;

    //    redis free tier needs to be refresh once in 15 days so that database i n redis not gets deleted
    //    runs every 14 day
    //    @Scheduled(fixedRate = 14L * 24 * 60 * 60 * 1000)
    public void refreshRedis() {
        try {
            for (int i = 0; i < 2; i++) {
                String city = "Delhi";
                WeatherResponse weatherForCity = weatherService.getWeatherForCity(city);
            }
            String reason = "Redis need to refresh at-least once in 15 days.";
            String msg = "Redis refreshed at time:" + LocalDateTime.now();
            log.info("{} {} {}", reason, "\n", msg);
        } catch (Exception e) {
            log.warn("Some error occurred while scheduled refresh of Redis. \nException:", e);
        }
    }
}
