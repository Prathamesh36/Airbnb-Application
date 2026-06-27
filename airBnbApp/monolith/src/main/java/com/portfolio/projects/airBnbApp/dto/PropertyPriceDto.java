package com.portfolio.projects.airBnbApp.dto;

import com.portfolio.projects.airBnbApp.entity.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyPriceDto {
    private Property Property;
    private Double price;
}
