package com.portfolio.projects.common.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PropertyDto {

    private Long id;

    private String name;

    private String city;

    private String[] photos;

    private String[] amenities;

    private ContactInfoDto contactInfo;

    private Boolean active;

}
