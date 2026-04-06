package com.portfolio.projects.airBnbApp.service;


import com.portfolio.projects.airBnbApp.dto.ProfileUpdateRequestDto;
import com.portfolio.projects.airBnbApp.dto.UserDto;
import com.portfolio.projects.airBnbApp.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();

}
