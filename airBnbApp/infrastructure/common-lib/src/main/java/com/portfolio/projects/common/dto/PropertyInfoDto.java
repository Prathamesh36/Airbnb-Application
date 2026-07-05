package com.portfolio.projects.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PropertyInfoDto {
    private PropertyDto Property;
    private List<RoomDto> rooms;
}
