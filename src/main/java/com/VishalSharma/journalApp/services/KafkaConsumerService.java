package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.model.SentimentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "weekly-sentiment", groupId = "weekly-sentiment-group")
    public void consumer(SentimentData data) {
        try {
            mySendMail(data);
            String email = data.getEmail();
            String msg = "Sending weekly analyzed sentiment mail";
            log.info("{} {} {}", msg, " to ", email);
        } catch (Exception e) {
            log.info("Couldn't able to sent weekly analyzed sentiment to {} Exception: ", data.getEmail(), e);
            throw new RuntimeException(e);
        }
    }

    private void mySendMail(SentimentData data) {
        try {
            emailService.sendMail(data.getEmail(), "Your Weekly analyzed sentiment", data.getSentiment().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
