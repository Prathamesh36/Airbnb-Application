package com.portfolio.projects.booking_service.client.dto;

import lombok.Data;

@Data
public class PropertyDto {
    private Long id;
    private String name;
    private String city;
    private Boolean active;
    private Long ownerId;
}
