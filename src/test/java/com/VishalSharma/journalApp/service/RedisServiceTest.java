package com.VishalSharma.journalApp.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.aot.DisabledInAotMode;

@SpringBootTest
public class RedisServiceTest {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    @Disabled
    void testRedisLocalConnection(){
        redisTemplate.opsForValue().set("height", "8");
        Object salary = redisTemplate.opsForValue().get("salary");
        int a = 65;
    }


//    @Test
//    @Disabled
//    void testConnection() {
//        try {
//            redisTemplate.opsForValue().set("connection_test", "success");
//            String result = redisTemplate.opsForValue().get("connection_test");
//            Assertions.assertEquals("success", result);
//            System.out.println("✅ Connection successful!");
//        } catch (Exception e) {
//            System.err.println("❌ Connection failed: " + e.getMessage());
//        }
//    }

}
