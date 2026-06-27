package com.portfolio.projects.propertyservice.service;


import com.portfolio.projects.propertyservice.dto.PropertyDto;
import com.portfolio.projects.propertyservice.dto.PropertyInfoDto;
import com.portfolio.projects.propertyservice.dto.PropertyInfoRequestDto;
import com.portfolio.projects.propertyservice.entity.Property;

import java.util.List;

public interface PropertyService {
    PropertyDto createNewProperty(PropertyDto PropertyDto);

    PropertyDto getPropertyById(Long id);

    PropertyDto updatePropertyById(Long id, PropertyDto PropertyDto);

    void deletePropertyById(Long id);

    void activateProperty(Long PropertyId);

    PropertyInfoDto getPropertyInfoById(Long PropertyId);

    List<PropertyDto> getAllPropertys();
}

