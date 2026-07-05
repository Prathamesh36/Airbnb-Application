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

    @GetMapping("/{PropertyId}")
    public ResponseEntity<PropertyDto> getPropertyById(@PathVariable Long PropertyId) {
        PropertyDto PropertyDto = PropertyService.getPropertyById(PropertyId);
        return ResponseEntity.ok(PropertyDto);
    }

    @PutMapping("/{PropertyId}")
    public ResponseEntity<PropertyDto> updatePropertyById(@PathVariable Long PropertyId, @RequestBody PropertyDto PropertyDto) {
        PropertyDto Property = PropertyService.updatePropertyById(PropertyId, PropertyDto);
        return ResponseEntity.ok(Property);
    }

    @DeleteMapping("/{PropertyId}")
    public ResponseEntity<Void> deletePropertyById(@PathVariable Long PropertyId) {
        PropertyService.deletePropertyById(PropertyId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{PropertyId}/activate")
    public ResponseEntity<Void> activateProperty(@PathVariable Long PropertyId) {
        PropertyService.activateProperty(PropertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PropertyDto>> getAllPropertys() {
        return ResponseEntity.ok(PropertyService.getAllPropertys());
    }



}

