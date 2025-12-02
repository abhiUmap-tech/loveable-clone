package com.projects.lovable_clone.dtos.project;

import com.projects.lovable_clone.dtos.auth.UserProfileResponse;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        UserProfileResponse userProfileResponse) {}
