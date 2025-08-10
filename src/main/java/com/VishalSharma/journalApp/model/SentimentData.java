package com.VishalSharma.journalApp.model;

import com.VishalSharma.journalApp.enums.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentData {
    private String email;
    private Sentiment sentiment;
}
