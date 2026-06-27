package com.portfolio.projects.booking_service.client.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomDto {
    private Long id;
    private String type;
    private BigDecimal basePrice;
    private Integer totalCount;
    private Integer capacity;
}
