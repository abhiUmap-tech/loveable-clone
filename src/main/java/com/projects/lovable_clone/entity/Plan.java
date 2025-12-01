package com.projects.lovable_clone.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan {

    Long id;

    String name;
    String stripePriceId;

    Integer maxProjects;
    Integer maxTokensPerDay;
    Integer maxPreview;

    Boolean unlimitedAI;
    Boolean active;
}
