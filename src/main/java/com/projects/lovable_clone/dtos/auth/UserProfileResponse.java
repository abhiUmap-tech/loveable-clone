package com.projects.lovable_clone.dtos.auth;

public record UserProfileResponse(
        Long id,
        String email,
        String name,
        String avatarUrl) {}
