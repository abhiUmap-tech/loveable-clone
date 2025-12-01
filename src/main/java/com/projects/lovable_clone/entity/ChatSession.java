package com.projects.lovable_clone.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatSession {

    Project project;

    User user;

    String title;

    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;//soft delete


}
