package com.portfolio.projects.inventoryservice.controller;

import com.portfolio.projects.common.dto.InventoryBookingDto;
import com.portfolio.projects.common.dto.ReserveInventoryResponse;
import com.portfolio.projects.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/inventory")
@RequiredArgsConstructor
public class InternalInventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/reserve")
    public ResponseEntity<ReserveInventoryResponse> reserveInventory(@RequestBody InventoryBookingDto bookingDto) {
        return ResponseEntity.ok(inventoryService.reserveInventory(bookingDto));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmInventory(@RequestBody InventoryBookingDto bookingDto) {
        inventoryService.confirmInventory(bookingDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseInventory(@RequestBody InventoryBookingDto bookingDto) {
        inventoryService.releaseInventory(bookingDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unreserve")
    public ResponseEntity<Void> unreserveInventory(@RequestBody InventoryBookingDto bookingDto) {
        inventoryService.unreserveInventory(bookingDto);
        return ResponseEntity.ok().build();
    }
}
