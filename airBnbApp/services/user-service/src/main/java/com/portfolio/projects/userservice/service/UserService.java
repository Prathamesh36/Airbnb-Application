package com.portfolio.projects.userservice.service;


import com.portfolio.projects.userservice.dto.ProfileUpdateRequestDto;
import com.portfolio.projects.userservice.dto.UserDto;
import com.portfolio.projects.userservice.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();

}
