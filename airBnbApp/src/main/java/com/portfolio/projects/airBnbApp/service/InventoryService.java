package com.portfolio.projects.airBnbApp.service;

import com.portfolio.projects.airBnbApp.dto.HotelDto;
import com.portfolio.projects.airBnbApp.dto.HotelSearchRequest;
import com.portfolio.projects.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
