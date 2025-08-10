package com.VishalSharma.journalApp.schedular;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserSchedulerTests {

    @Autowired
    private UserSchedular userSchedular;

    @Test
    @Disabled
    public void fetchUserAndSendSAMailTest(){
        userSchedular.fetchUserAndSendSAMail();
    }
}
