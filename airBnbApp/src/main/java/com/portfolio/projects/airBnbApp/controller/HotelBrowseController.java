package com.portfolio.projects.airBnbApp.controller;

import com.portfolio.projects.airBnbApp.dto.HotelDto;
import com.portfolio.projects.airBnbApp.dto.HotelInfoDto;
import com.portfolio.projects.airBnbApp.dto.HotelSearchRequest;
import com.portfolio.projects.airBnbApp.service.HotelService;
import com.portfolio.projects.airBnbApp.service.impl.InventoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private static final Logger log = LoggerFactory.getLogger(HotelBrowseController.class);
    private final InventoryServiceImpl inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        log.info("Received hotel search request: {}", hotelSearchRequest);
        Page<HotelDto> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        log.info("Received request for hotel info with ID: {}", hotelId);
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
