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
        List<User> userWithSA = userRepository.findUserWithSA();
        for (User user : userWithSA) {
//                extracting journalEntries of user
            List<Sentiment> list = user.getJournalEntries().stream().filter(j -> j.getDate().isAfter(LocalDateTime.now().minusWeeks(1))).map(JournalEntry::getSentiment).toList();

//               a map to store sentiment and it's occurrence count
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
//          integrating kafka for automation
            if (mostFrequentSentiment != null) {
                SentimentData data = SentimentData.builder().email(user.getEmail()).sentiment(mostFrequentSentiment).build();
                try {
                    kafkaTemplate.send("weekly-sentiment", user.getEmail(), data);
                } catch (Exception e) {
//                  kafka fallback
                    log.warn("For some reason KafkaTemplate failed, sending mail to users manually.", e);
                    emailService.sendMail(data.getEmail(), "Your Weekly analyzed sentiment", data.getSentiment().toString());
                }
            }
        }
    }


    @Scheduled(cron = "0 0/10 * * * *")
    public void refreshAppCache() {
        appCache.init();
    }


}
