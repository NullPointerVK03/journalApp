package com.VishalSharma.journalApp.jwt;

import com.VishalSharma.journalApp.utils.JwtUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtUtiltest {


    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @Disabled
    void testToCheckSecretKeyOfJWT() {
        jwtUtil.generateToken("Ram");
    }


}
