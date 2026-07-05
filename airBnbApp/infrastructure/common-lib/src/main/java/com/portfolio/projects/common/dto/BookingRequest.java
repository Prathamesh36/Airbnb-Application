package com.portfolio.projects.common.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long PropertyId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomsCount;
}
