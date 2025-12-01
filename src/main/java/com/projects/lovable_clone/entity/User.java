package com.projects.lovable_clone.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    Long id;

    String email;
    String passwordHash;
    String name;
    String avatarUrl;

    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;//soft delete


}
