package com.portfolio.projects.booking_service.dto;


import com.portfolio.projects.booking_service.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private Long userId;
    private String name;
    private Gender gender;
    private Integer age;
}
