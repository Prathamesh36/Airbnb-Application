package com.portfolio.projects.propertyservice.controller;

import com.portfolio.projects.propertyservice.dto.PropertyDto;
import com.portfolio.projects.propertyservice.dto.RoomDto;
import com.portfolio.projects.propertyservice.service.PropertyService;
import com.portfolio.projects.propertyservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalPropertyController {

    private final PropertyService propertyService;
    private final RoomService roomService;

    @GetMapping("/properties/{propertyId}")
    public ResponseEntity<PropertyDto> getPropertyById(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.getPropertyById(propertyId));
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }
}
