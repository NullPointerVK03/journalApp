package com.VishalSharma.journalApp.service;

import com.VishalSharma.journalApp.services.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String senderMail;

    //    include this property in your own .properties or .yml file
    @Value("${dummy.email}")
    private String dummyReceiversEmail;

    @Disabled
    @Test
    void sentMailTest() {
//
        Assertions.assertTrue(
                emailService.sendMail(
                        dummyReceiversEmail,
                        "Testing JavaMailSender",
                        "java mail service ko test kar raha hun. \n" +
                                "sending mail from: " + senderMail
                ));
    }

}
