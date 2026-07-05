package com.portfolio.projects.common.dto;


import com.portfolio.projects.common.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private Long userId;
    private String name;
    private Gender gender;
    private Integer age;
}
