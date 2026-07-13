package com.portfolio.projects.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertySearchEvent {
    private Long propertyId;
    private String name;
    private String city;
    private Boolean active;
}
