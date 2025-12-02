package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.auth.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile(Long userId);
}
