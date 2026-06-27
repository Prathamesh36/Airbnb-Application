package com.portfolio.projects.propertyservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PropertyInfoRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long roomsCount;
}
