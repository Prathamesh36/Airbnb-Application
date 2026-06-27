package com.portfolio.projects.propertyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreatedEvent {
    private Long propertyId;
    private Long roomId;
    private Integer totalCount;
    private BigDecimal basePrice;
    private String city;
}
