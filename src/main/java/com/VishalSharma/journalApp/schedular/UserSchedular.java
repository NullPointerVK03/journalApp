package com.VishalSharma.journalApp.schedular;

import com.VishalSharma.journalApp.appCache.AppCache;
import com.VishalSharma.journalApp.entity.JournalEntry;
import com.VishalSharma.journalApp.entity.User;
import com.VishalSharma.journalApp.enums.Sentiment;
import com.VishalSharma.journalApp.model.SentimentData;
import com.VishalSharma.journalApp.repository.UserRepositoryImpl;
import com.VishalSharma.journalApp.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserSchedular {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUserAndSendSAMail() {
        log.info("Starting scheduled task: fetchUserAndSendSAMail at {}", LocalDateTime.now());
        List<User> userWithSA = userRepository.findUserWithSA();
        log.info("Found {} users opted in for Sentiment Analysis", userWithSA.size());

        for (User user : userWithSA) {
            log.info("Processing user with username: {} and email: {}", user.getUserName(), user.getEmail());

            // extracting journalEntries of user
            List<Sentiment> list = user.getJournalEntries().stream()
                    .filter(j -> j.getDate().isAfter(LocalDateTime.now().minusWeeks(1)))
                    .map(JournalEntry::getSentiment)
                    .toList();
            log.info("Found {} journal entries from last week for user: {}", list.size(), user.getUserName());

            // a map to store sentiment and its occurrence count
            Map<Sentiment, Integer> sentimentIntegerMap = new HashMap<>();

            Sentiment mostFrequentSentiment = null;
            int maxCnt = 0;

            for (Sentiment sentiment : list) {
                if (sentiment != null) {
                    Integer sentimentCount = sentimentIntegerMap.getOrDefault(sentiment, 0);
                    sentimentIntegerMap.put(sentiment, sentimentCount + 1);
                    Integer currCnt = sentimentIntegerMap.get(sentiment);
                    if (currCnt > maxCnt) {
                        maxCnt = currCnt;
                        mostFrequentSentiment = sentiment;
                    }
                }
            }
            log.info("Most frequent sentiment for user {} is: {}", user.getUserName(), mostFrequentSentiment);

            // integrating kafka for automation
            if (mostFrequentSentiment != null) {
                SentimentData data = SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment(mostFrequentSentiment)
                        .build();

                try {
                    log.info("Sending sentiment data to Kafka topic 'weekly-sentiment' for user: {}", user.getUserName());
                    kafkaTemplate.send("weekly-sentiment", user.getEmail(), data);
                    log.info("Sentiment data successfully sent to Kafka for user: {}", user.getUserName());
                } catch (Exception e) {
                    // kafka fallback
                    log.warn("KafkaTemplate failed for user: {}, falling back to sending email manually", user.getUserName(), e);
                    emailService.sendMail(data.getEmail(), "Your Weekly analyzed sentiment", data.getSentiment().toString());
                    log.info("Sentiment email manually sent to {}", data.getEmail());
                }
            } else {
                log.info("No valid sentiment found for user: {} in last week's journal entries", user.getUserName());
            }
        }
        log.info("Scheduled task: fetchUserAndSendSAMail completed at {}", LocalDateTime.now());
    }
}
