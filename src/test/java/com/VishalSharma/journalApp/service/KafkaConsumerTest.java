package com.VishalSharma.journalApp.service;

import com.VishalSharma.journalApp.schedular.UserSchedular;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KafkaConsumerTest {

    @Autowired
    private UserSchedular userSchedular;

    @Test
    @Disabled
    public void test(){
        userSchedular.fetchUserAndSendSAMail();
    }
}
