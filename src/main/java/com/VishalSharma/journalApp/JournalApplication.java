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
import java.nio.file.Paths;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class JournalApplication {
    public static void main(String[] args) {
//        my custom logic to create a separate folder for logs of current application
        // Resolve log directory path: either from -DLOG_DIR or default to <project-root>/journalAppFileLogs
        String logDirPath = System.getProperty("LOG_DIR",
                Paths.get(System.getProperty("user.dir"), "journalApp/journalAppFileLogs").toString());


        // Create log directory if it doesn't exist
        File logDir = new File(logDirPath);
        if (!logDir.exists()) {
            logDir.mkdirs();
            log.info("Created log directory: {}", logDir.getAbsolutePath());
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
