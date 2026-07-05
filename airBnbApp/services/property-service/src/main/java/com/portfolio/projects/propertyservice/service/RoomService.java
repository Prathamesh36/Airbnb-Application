package com.portfolio.projects.propertyservice.service;

import com.portfolio.projects.common.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long PropertyId, RoomDto roomDto);

    List<RoomDto> getAllRoomsInProperty(Long PropertyId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);

    RoomDto updateRoomById(Long PropertyId, Long roomId, RoomDto roomDto);
}
