package com.portfolio.projects.propertyservice.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class PropertyContactInfo {
    private String address;
    private String phoneNumber;
    private String email;
    private String location;
}
