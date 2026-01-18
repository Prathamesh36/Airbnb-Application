package com.portfolio.projects.airBnbApp.service.impl;

import com.portfolio.projects.airBnbApp.dto.RoomDto;
import com.portfolio.projects.airBnbApp.entity.Hotel;
import com.portfolio.projects.airBnbApp.entity.Room;
import com.portfolio.projects.airBnbApp.exception.ResourceNotFoundException;
import com.portfolio.projects.airBnbApp.repository.HotelRepository;
import com.portfolio.projects.airBnbApp.repository.RoomRepository;
import com.portfolio.projects.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryServiceImpl inventoryService;
    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelId);

        // Verify that the hotel exists
        Hotel hotel = hotelRepository.
                findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        log.info("Room created with ID: {}", room.getId());
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
            log.info("Initialized inventory for room ID: {} for one year", room.getId());
        }
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Fetching all rooms in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.
                findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
        log.info("Found {} rooms in hotel with ID: {}", hotel.getRooms().size(), hotelId);
        return hotel.getRooms()
                .stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Fetching room with ID: {}", roomId);
        Room room = roomRepository.
                findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));
        log.info("Room found with ID: {}", roomId);
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public void deleteRoomById(Long roomId) {
        log.info("Deleting room with ID: {}", roomId);

        Room room = roomRepository.
                findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        roomRepository.deleteById(roomId);
        log.info("Room deleted with ID: {}", roomId);

        inventoryService.deleteFutureInventories(room);
    }
}
