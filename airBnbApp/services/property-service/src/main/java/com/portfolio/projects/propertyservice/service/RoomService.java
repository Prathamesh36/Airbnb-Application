package com.portfolio.projects.propertyservice.service;

import com.portfolio.projects.common.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long propertyId, RoomDto roomDto);

    List<RoomDto> getAllRoomsInProperty(Long propertyId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);

    RoomDto updateRoomById(Long propertyId, Long roomId, RoomDto roomDto);
}
