package com.portfolio.projects.propertyservice.dto;

import com.portfolio.projects.propertyservice.entity.PropertyContactInfo;
import com.portfolio.projects.propertyservice.entity.Room;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PropertyDto {

    private Long id;

    private String name;

    private String city;

    private String[] photos;

    private String[] amenities;

    private PropertyContactInfo contactInfo;

    private Boolean active;

}
