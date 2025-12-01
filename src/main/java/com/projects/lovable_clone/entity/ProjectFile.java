package com.projects.lovable_clone.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectFile {

    Long id;

    Project project;

    String path;
    String minioObjectKey;

    Instant createdAt;
    Instant updatedAt;

    User createdBy;
    User updatedBy;





}
