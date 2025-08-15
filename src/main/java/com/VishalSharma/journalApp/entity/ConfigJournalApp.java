package com.VishalSharma.journalApp.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Config-journal-app")
public class ConfigJournalApp {
    private String key;
    private String value;
}
