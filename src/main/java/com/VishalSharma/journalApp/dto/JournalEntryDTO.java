package com.VishalSharma.journalApp.dto;

import com.VishalSharma.journalApp.enums.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryDTO {

    private String title;
    private String content;
    private Sentiment sentiment;
}
