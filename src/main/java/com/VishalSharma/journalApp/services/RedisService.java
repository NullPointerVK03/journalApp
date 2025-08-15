package com.VishalSharma.journalApp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //    generic getter
    public <T> T get(String key, Class<T> entityClass) {
        try {
            log.info("Fetching value from Redis for key: {}", key);
            Object o = redisTemplate.opsForValue().get(key);
            if (o != null) {
                log.info("Value found in Redis for key: {}", key);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(o.toString(), entityClass);
            }
            log.info("No value found in Redis for key: {}", key);
            return null;
        } catch (Exception e) {
            log.error("Exception occurred while fetching value from Redis for key: {}", key, e);
            return null;
        }
    }

    //    generic setter
    public void set(String key, Object o, Long ttl) {
        try {
            log.info("Saving value to Redis for key: {} with TTL: {} seconds", key, ttl);
            ObjectMapper mapper = new ObjectMapper();
            String jsonValue = mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
            log.info("Value saved to Redis for key: {}", key);
        } catch (Exception e) {
            log.error("Exception occurred while saving value to Redis for key: {}", key, e);
        }
    }
}