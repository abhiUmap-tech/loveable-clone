package com.projects.lovable_clone.dtos.auth;

public record AuthResponse(
        String token,
        UserProfileResponse userProfileResponse) {}


