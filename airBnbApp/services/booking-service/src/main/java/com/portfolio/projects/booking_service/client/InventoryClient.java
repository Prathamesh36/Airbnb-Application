package com.portfolio.projects.booking_service.client;

import com.portfolio.projects.booking_service.client.dto.InventoryBookingDto;
import com.portfolio.projects.booking_service.client.dto.ReserveInventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/internal/inventory/reserve")
    ReserveInventoryResponse reserveInventory(@RequestBody InventoryBookingDto bookingDto);

    @PostMapping("/internal/inventory/confirm")
    void confirmInventory(@RequestBody InventoryBookingDto bookingDto);

    @PostMapping("/internal/inventory/release")
    void releaseInventory(@RequestBody InventoryBookingDto bookingDto);

    @PostMapping("/internal/inventory/unreserve")
    void unreserveInventory(@RequestBody InventoryBookingDto bookingDto);
}
