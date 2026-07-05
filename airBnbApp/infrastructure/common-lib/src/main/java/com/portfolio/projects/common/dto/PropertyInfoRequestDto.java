package com.portfolio.projects.common.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PropertyInfoRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long roomsCount;
}
