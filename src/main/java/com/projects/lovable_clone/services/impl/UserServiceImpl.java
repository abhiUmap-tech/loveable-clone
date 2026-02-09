package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.auth.UserProfileResponse;
import com.projects.lovable_clone.error.ResourceNotFoundException;
import com.projects.lovable_clone.repository.UserRepository;
import com.projects.lovable_clone.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }
}
