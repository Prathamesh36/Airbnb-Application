package com.portfolio.projects.authservice.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
}
