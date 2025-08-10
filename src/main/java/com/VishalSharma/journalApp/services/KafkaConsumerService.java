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

        } catch (Exception e) {
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
