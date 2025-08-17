package com.VishalSharma.journalApp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Generic getter
     *
     * @param key         Redis key
     * @param entityClass Type of entity to deserialize into
     * @param <T>         Generic type
     * @return Deserialized object or null if not found
     */
    public <T> T get(String key, Class<T> entityClass) {
        try {
            log.info("Fetching value from Redis for key: {}", key);
            Object o = redisTemplate.opsForValue().get(key);

            if (o != null) {
                log.info("Value found in Redis for key: {}", key);
                return objectMapper.readValue(o.toString(), entityClass);
            }

            log.info("No value found in Redis for key: {}", key);
            return null;
        } catch (Exception e) {
            log.error("Error while fetching value from Redis for key: {}", key, e);
            return null; // fallback safe
        }
    }

    /**
     * Generic setter
     *
     * @param key Redis key
     * @param o   Object to store
     * @param ttl TTL in seconds
     */
    public void set(String key, Object o, Long ttl) {
        try {
            log.info("Saving value to Redis for key: {} with TTL: {} seconds", key, ttl);
            String jsonValue = objectMapper.writeValueAsString(o);

            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);

            log.info("Value saved to Redis for key: {}", key);
        } catch (Exception e) {
            log.error("Error while saving value to Redis for key: {}", key, e);
        }
    }
}
