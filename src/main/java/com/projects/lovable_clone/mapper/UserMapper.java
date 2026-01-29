package com.projects.lovable_clone.mapper;

import com.projects.lovable_clone.dtos.auth.UserProfileResponse;
import com.projects.lovable_clone.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserProfileResponse userToUserProfileResponse(User user);
}
