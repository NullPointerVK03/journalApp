package com.VishalSharma.journalApp.dto;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NonNull
    @Indexed(unique = true)
    private String userName;


    @NonNull
    private String password;

    private boolean sentimentAnalysis;

    @Indexed(unique = true)
    private String email;

}
