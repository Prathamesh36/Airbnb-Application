package com.portfolio.projects.userservice.controller;


import com.portfolio.projects.userservice.dto.ProfileUpdateRequestDto;
import com.portfolio.projects.userservice.dto.UserDto;

import com.portfolio.projects.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        userService.updateProfile(profileUpdateRequestDto);

        return ResponseEntity.noContent().build();
    }



    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

}
