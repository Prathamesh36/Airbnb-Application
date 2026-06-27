package com.portfolio.projects.authservice.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}
