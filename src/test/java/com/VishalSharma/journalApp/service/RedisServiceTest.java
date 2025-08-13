package com.VishalSharma.journalApp.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    @Disabled
    void testRedisLocalConnection(){
//        checking compatibility with local redis environment
        redisTemplate.opsForValue().set("height", "8");
        Object salary = redisTemplate.opsForValue().get("salary");
    }


//    @Test
//    @Disabled
//    void testConnection() {
//        try {
//            redisTemplate.opsForValue().set("connection_test", "success");
//            String result = redisTemplate.opsForValue().get("connection_test");
//            Assertions.assertEquals("success", result);
//            System.out.println("Connection successful!");
//        } catch (Exception e) {
//            System.err.println("Connection failed: " + e.getMessage());
//        }
//    }

}
