package com.portfolio.projects.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PropertyPriceDto {
    private Long propertyId;
    private BigDecimal price;
}
