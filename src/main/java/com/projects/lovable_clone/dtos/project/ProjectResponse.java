package com.projects.lovable_clone.dtos.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.projects.lovable_clone.dtos.auth.UserProfileResponse;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        UserProfileResponse userProfileResponse) {}
