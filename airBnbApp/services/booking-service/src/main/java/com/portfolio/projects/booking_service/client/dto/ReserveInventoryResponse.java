package com.portfolio.projects.booking_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveInventoryResponse {
    private BigDecimal priceForOneRoom;
    private BigDecimal totalPrice;
}
