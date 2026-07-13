package com.portfolio.projects.booking_service.client;

import com.portfolio.projects.booking_service.client.dto.PropertyDto;
import com.portfolio.projects.booking_service.client.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "property-service")
public interface PropertyClient {

    @GetMapping("/internal/properties/{propertyId}")
    PropertyDto getPropertyById(@PathVariable Long propertyId);

    @GetMapping("/internal/rooms/{roomId}")
    RoomDto getRoomById(@PathVariable Long roomId);
}
