package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.auth.AuthResponse;
import com.projects.lovable_clone.dtos.auth.LoginRequest;
import com.projects.lovable_clone.dtos.auth.SignupRequest;
import com.projects.lovable_clone.services.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthResponse signup(SignupRequest signupRequest) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        return null;
    }
}
