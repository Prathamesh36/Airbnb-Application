package com.portfolio.projects.propertyservice.service;


import com.portfolio.projects.common.dto.PropertyDto;
import com.portfolio.projects.common.dto.PropertyInfoDto;
import com.portfolio.projects.common.dto.PropertyInfoRequestDto;
import com.portfolio.projects.propertyservice.entity.Property;

import java.util.List;

public interface PropertyService {
    PropertyDto createNewProperty(PropertyDto PropertyDto);

    PropertyDto getPropertyById(Long id);

    PropertyDto getInternalPropertyById(Long id);

    PropertyDto updatePropertyById(Long id, PropertyDto PropertyDto);

    void deletePropertyById(Long id);

    void activateProperty(Long propertyId);

    PropertyInfoDto getPropertyInfoById(Long propertyId);

    List<PropertyDto> getAllPropertys();
}

