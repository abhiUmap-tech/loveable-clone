package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.auth.UserProfileResponse;
import com.projects.lovable_clone.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }
}
