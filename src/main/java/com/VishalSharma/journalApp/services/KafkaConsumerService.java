package com.VishalSharma.journalApp.services;

import com.VishalSharma.journalApp.model.SentimentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private final KafkaTemplate<String, SentimentData> kafkaTemplate;
    private final EmailService emailService;

    public KafkaConsumerService(KafkaTemplate<String, SentimentData> kafkaTemplate, EmailService emailService) {
        this.kafkaTemplate = kafkaTemplate;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "weekly-sentiment", groupId = "weekly-sentiment-group")
    public void consumer(SentimentData data) {
        try {
            log.info("Received SentimentData from Kafka topic 'weekly-sentiment' for email: {}", data.getEmail());
            mySendMail(data);
            log.info("Successfully processed weekly sentiment email for: {}", data.getEmail());
        } catch (Exception e) {
            log.error("Failed to process weekly sentiment email for: {}. Exception: ", data.getEmail(), e);
            throw new RuntimeException("Error while processing sentiment data for: " + data.getEmail(), e);
        }
    }

    private void mySendMail(SentimentData data) {
        try {
            log.info("Sending weekly analyzed sentiment email to {}", data.getEmail());
            emailService.sendMail(
                    data.getEmail(),
                    "Your Weekly Analyzed Sentiment",
                    data.getSentiment().toString()
            );
            log.info("Weekly analyzed sentiment email sent successfully to {}", data.getEmail());
        } catch (Exception e) {
            log.error("Error occurred while sending weekly analyzed sentiment email to {}. Exception: ",
                    data.getEmail(), e);
            throw new RuntimeException("Error while sending email to " + data.getEmail(), e);
        }
    }
}
