package com.VishalSharma.journalApp.entity;

import com.VishalSharma.journalApp.enums.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "journal_entries")
public class JournalEntry {

    @Id
    private ObjectId id;
    private String title;
    private String content;
    private LocalDateTime date;
    private Sentiment sentiment;
}
