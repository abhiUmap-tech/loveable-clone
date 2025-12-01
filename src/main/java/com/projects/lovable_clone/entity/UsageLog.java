package com.projects.lovable_clone.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageLog {
    Long id;

    User user;

    Project project;

    String action;
    String metaData;//JSON of {model_used, prompt_used}

    Integer tokensUsed;
    Integer durationsMs;

    Instant createdAt;




}
