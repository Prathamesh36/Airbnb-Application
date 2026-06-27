package com.portfolio.projects.airBnbApp.client;

import com.portfolio.projects.airBnbApp.client.dto.InventoryBookingDto;
import com.portfolio.projects.airBnbApp.client.dto.ReserveInventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", url = "${inventory.service.url:http://localhost:8083}")
public interface InventoryClient {

    @PostMapping("/internal/inventory/reserve")
    ReserveInventoryResponse reserveInventory(@RequestBody InventoryBookingDto bookingDto);

    @PostMapping("/internal/inventory/confirm")
    void confirmInventory(@RequestBody InventoryBookingDto bookingDto);

    @PostMapping("/internal/inventory/release")
    void releaseInventory(@RequestBody InventoryBookingDto bookingDto);
}
