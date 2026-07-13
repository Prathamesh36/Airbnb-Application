package com.portfolio.projects.inventoryservice.service;

import com.portfolio.projects.common.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(RoomCreatedEvent room);

    void deleteAllInventories(Long roomId);

    Page<PropertyPriceDto> searchPropertys(PropertySearchRequest PropertySearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);

    ReserveInventoryResponse reserveInventory(InventoryBookingDto bookingDto);

    void confirmInventory(InventoryBookingDto bookingDto);

    void releaseInventory(InventoryBookingDto bookingDto);

    void unreserveInventory(InventoryBookingDto bookingDto);
}
