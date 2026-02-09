package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.auth.AuthResponse;
import com.projects.lovable_clone.dtos.auth.LoginRequest;
import com.projects.lovable_clone.dtos.auth.SignupRequest;
import com.projects.lovable_clone.entity.User;
import com.projects.lovable_clone.error.BadRequestException;
import com.projects.lovable_clone.mapper.UserMapper;
import com.projects.lovable_clone.repository.UserRepository;
import com.projects.lovable_clone.security.AuthUtil;
import com.projects.lovable_clone.services.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest signupRequest) {
        userRepository.findByUsername(signupRequest.username())
                .ifPresent(user -> {
                    throw new BadRequestException("User already exist with the username::" + signupRequest.username());
                });

        var user = userMapper.toEntity(signupRequest);
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        userRepository.save(user);

        return new AuthResponse(authUtil.generateToken(user), userMapper.userToUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try{
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()));

        // If we reached here, authentication is successful
        User user = (User) authentication.getPrincipal();
        var jwtToken = authUtil.generateToken(user);

        return new AuthResponse(jwtToken,
                userMapper.userToUserProfileResponse(user));

        }catch (BadCredentialsException badCredentialsException){
            throw new BadRequestException("Invalid username or password");
        }

    }
}
