package com.portfolio.projects.propertyservice.controller;

import com.portfolio.projects.common.dto.RoomDto;
import com.portfolio.projects.propertyservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/properties/{propertyId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long propertyId, @RequestBody RoomDto roomDto){
        RoomDto room = roomService.createNewRoom(propertyId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<java.util.List<RoomDto>> getAllRoomsInProperty(@PathVariable Long propertyId){
        List<RoomDto> rooms = roomService.getAllRoomsInProperty(propertyId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long propertyId, @PathVariable Long roomId){
        RoomDto room = roomService.getRoomById(roomId);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long propertyId, @PathVariable Long roomId){
        roomService.deleteRoomById(roomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoomById(@PathVariable Long propertyId, @PathVariable Long roomId,
                                                  @RequestBody RoomDto roomDto) {
        return ResponseEntity.ok(roomService.updateRoomById(propertyId, roomId, roomDto));
    }
}
