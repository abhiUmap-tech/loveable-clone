package com.projects.lovable_clone.entity;

import com.projects.lovable_clone.enums.PreviewStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preview {

    Long id;

    Project project;

    String namespace;
    String podName;
    String previewUrl;

    Instant startedAt;
    Instant terminatedAt;

    Instant createdAt;

    PreviewStatus previewStatus;

}
