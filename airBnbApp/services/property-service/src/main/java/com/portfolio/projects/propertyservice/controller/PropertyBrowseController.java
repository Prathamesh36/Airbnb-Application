package com.portfolio.projects.propertyservice.controller;

import com.portfolio.projects.common.dto.PropertyDto;
import com.portfolio.projects.common.dto.PropertyInfoDto;
import com.portfolio.projects.propertyservice.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyBrowseController {

    private static final Logger log = LoggerFactory.getLogger(PropertyBrowseController.class);


    private final PropertyService PropertyService;

    @GetMapping("/{propertyId}/info")
    public ResponseEntity<PropertyInfoDto> getPropertyInfo(@PathVariable Long propertyId) {
        log.info("Received request for Property info with ID: {}", propertyId);
        return ResponseEntity.ok(PropertyService.getPropertyInfoById(propertyId));
    }
}
