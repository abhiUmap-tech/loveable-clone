package com.projects.lovable_clone.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Project {

    Long id;

    String name;

    User owner;

    Boolean isPublic = false;

    Instant createdAt;
    Instant updatedAt;
    Instant deleteAt; //soft delete

}
