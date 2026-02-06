package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.auth.AuthResponse;
import com.projects.lovable_clone.dtos.auth.LoginRequest;
import com.projects.lovable_clone.dtos.auth.SignupRequest;
import com.projects.lovable_clone.error.BadRequestException;
import com.projects.lovable_clone.mapper.UserMapper;
import com.projects.lovable_clone.repository.UserRepository;
import com.projects.lovable_clone.services.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signup(SignupRequest signupRequest) {
        userRepository.findByUsername(signupRequest.username())
                .ifPresent(user -> {
                    throw new BadRequestException("User already exist with the username::" + signupRequest.username());
                });

        var user = userMapper.toEntity(signupRequest);
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        userRepository.save(user);

        return new AuthResponse("dummy", userMapper.userToUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        return null;
    }
}
