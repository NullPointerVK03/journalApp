package com.VishalSharma.journalApp.appCache;

import com.VishalSharma.journalApp.entity.ConfigJournalApp;
import com.VishalSharma.journalApp.repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum keys{
        WEATHER_API
    }


    public Map<String, String> appCache;

    @Autowired
    ConfigJournalAppRepository configJournalAppRepository;

    @PostConstruct
    public void init() {
        appCache = new HashMap<>();
        List<ConfigJournalApp> all = configJournalAppRepository.findAll();
        for (ConfigJournalApp configJournalApp : all) {
            appCache.put(configJournalApp.getKey(), configJournalApp.getValue());
        }
    }


}
