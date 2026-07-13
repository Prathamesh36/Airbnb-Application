package com.portfolio.projects.propertyservice.controller;

import com.portfolio.projects.common.dto.PropertyDto;
import com.portfolio.projects.propertyservice.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/properties")
@RequiredArgsConstructor
@Slf4j
public class PropertyController {

    private final PropertyService PropertyService;


    @PostMapping
    public ResponseEntity<PropertyDto> createNewProperty(@RequestBody PropertyDto PropertyDto) {
        log.info("Attempting to create a new Property with name: "+PropertyDto.getName());
        PropertyDto Property = PropertyService.createNewProperty(PropertyDto);
        return new ResponseEntity<>(Property, HttpStatus.CREATED);
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyDto> getPropertyById(@PathVariable Long propertyId) {
        PropertyDto PropertyDto = PropertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(PropertyDto);
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<PropertyDto> updatePropertyById(@PathVariable Long propertyId, @RequestBody PropertyDto PropertyDto) {
        PropertyDto Property = PropertyService.updatePropertyById(propertyId, PropertyDto);
        return ResponseEntity.ok(Property);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> deletePropertyById(@PathVariable Long propertyId) {
        PropertyService.deletePropertyById(propertyId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{propertyId}/activate")
    public ResponseEntity<Void> activateProperty(@PathVariable Long propertyId) {
        PropertyService.activateProperty(propertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PropertyDto>> getAllPropertys() {
        return ResponseEntity.ok(PropertyService.getAllPropertys());
    }



}

