package com.VishalSharma.journalApp.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Document(collection = "users")
public class User {

    /*
     * since we are creating a user using @Builder
     * it is setting all the fields as null which results in null pointer exception*/

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    private String userName;

    @NonNull
    private String password;

    @Indexed(unique = true)
    private String email;

    //    if optIn-> send weekly sentiment analysis to user on his mail id
    private boolean sentimentAnalysis;

    @DBRef
    private List<JournalEntry> journalEntries = new ArrayList<>();

    private List<String> roles = new ArrayList<>();


}
