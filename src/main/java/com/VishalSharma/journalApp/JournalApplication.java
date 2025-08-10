package com.VishalSharma.journalApp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class JournalApplication {
    public static void main(String[] args) {
//        my custom logic to create a separate folder for logs of current application
        File logDir = new File("./journalApp/journalAppFileLogs");
        if (!logDir.exists()) {
            logDir.mkdirs();
            System.out.println("Created log directory: " + logDir.getAbsolutePath());
        }
        SpringApplication.run(JournalApplication.class, args);
        log.info("JOURNAL APP STARTED");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PlatformTransactionManager methodName(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

}
