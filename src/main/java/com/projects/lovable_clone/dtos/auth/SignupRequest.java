package com.projects.lovable_clone.dtos.auth;

public record SignupRequest(
        String email,
        String name,
        String password) {}
