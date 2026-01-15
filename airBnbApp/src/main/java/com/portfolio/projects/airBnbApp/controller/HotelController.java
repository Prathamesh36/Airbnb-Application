package com.portfolio.projects.airBnbApp.controller;

import com.portfolio.projects.airBnbApp.dto.HotelDto;
import com.portfolio.projects.airBnbApp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody HotelDto hotelDto) {
        log.info("Received request to create hotel: {}", hotelDto.getName());
        HotelDto hotel = hotelService.createNewHotel(hotelDto);
        log.info("Hotel created with ID: {}", hotel.getId());
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        log.info("Received request to fetch hotel with ID: {}", hotelId);
        HotelDto hotel = hotelService.getHotelById(hotelId);
        log.info("Returning hotel: {}", hotel.getName());
        return new ResponseEntity<>(hotel, HttpStatus.OK);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId, @RequestBody HotelDto hotelDto) {
        log.info("Received request to update hotel with ID: {}", hotelId);
        HotelDto hotel = hotelService.updateHotelById(hotelId, hotelDto);
        log.info("Hotel updated: {}", hotel.getName());
        return new ResponseEntity<>(hotel, HttpStatus.OK);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId) {
        log.info("Received request to delete hotel with ID: {}", hotelId);
        hotelService.deleteHotelById(hotelId);
        log.info("Hotel deleted with ID: {}", hotelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
