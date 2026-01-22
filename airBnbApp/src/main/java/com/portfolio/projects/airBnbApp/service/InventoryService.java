package com.portfolio.projects.airBnbApp.service;

import com.portfolio.projects.airBnbApp.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);
}
