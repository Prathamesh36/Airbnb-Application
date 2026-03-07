package com.portfolio.projects.airBnbApp.dto;

import com.portfolio.projects.airBnbApp.entity.User;
import com.portfolio.projects.airBnbApp.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
