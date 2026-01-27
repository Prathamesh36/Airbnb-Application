package com.portfolio.projects.airBnbApp.controller;

import com.portfolio.projects.airBnbApp.dto.HotelDto;
import com.portfolio.projects.airBnbApp.dto.HotelSearchRequest;
import com.portfolio.projects.airBnbApp.service.impl.InventoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private static final Logger log = LoggerFactory.getLogger(HotelBrowseController.class);
    private final InventoryServiceImpl inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        log.info("Received hotel search request: {}", hotelSearchRequest);
        Page<HotelDto> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }
}
