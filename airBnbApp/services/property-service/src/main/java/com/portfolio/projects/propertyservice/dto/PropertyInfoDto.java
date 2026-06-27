package com.portfolio.projects.propertyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PropertyInfoDto {
    private PropertyDto Property;
    private List<RoomDto> rooms;
}
