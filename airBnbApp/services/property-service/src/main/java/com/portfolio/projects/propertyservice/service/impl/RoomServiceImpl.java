package com.portfolio.projects.propertyservice.service.impl;

import com.portfolio.projects.propertyservice.dto.RoomDto;
import com.portfolio.projects.propertyservice.entity.Property;
import com.portfolio.projects.propertyservice.entity.Room;
import com.portfolio.projects.propertyservice.entity.User;
import com.portfolio.projects.propertyservice.exception.ResourceNotFoundException;
import com.portfolio.projects.propertyservice.exception.UnAuthorisedException;
import com.portfolio.projects.propertyservice.repository.PropertyRepository;
import com.portfolio.projects.propertyservice.repository.RoomRepository;

import com.portfolio.projects.propertyservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.projects.propertyservice.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final PropertyRepository PropertyRepository;

    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long PropertyId, RoomDto roomDto) {
        log.info("Creating a new room in Property with ID: {}", PropertyId);
        Property Property = PropertyRepository
                .findById(PropertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+PropertyId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(Property.getOwner())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+PropertyId);
        }

        Room room = modelMapper.map(roomDto, Room.class);
        room.setProperty(Property);
        room = roomRepository.save(room);

        if (Property.getActive()) {
            // inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInProperty(Long PropertyId) {
        log.info("Getting all rooms in Property with ID: {}", PropertyId);
        Property Property = PropertyRepository
                .findById(PropertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+PropertyId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(Property.getOwner())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+PropertyId);
        }

        return Property.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));
        return modelMapper.map(room, RoomDto.class);
    }

    @Transactional
    @Override
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getProperty().getOwner())) {
            throw new UnAuthorisedException("This user does not own this room with id: "+roomId);
        }

        // inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }

    @Override
    @Transactional
    public RoomDto updateRoomById(Long PropertyId, Long roomId, RoomDto roomDto) {
        log.info("Updating the room with ID: {}", roomId);
        Property Property = PropertyRepository
                .findById(PropertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+PropertyId));

        User user = getCurrentUser();
        if(!user.equals(Property.getOwner())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+PropertyId);
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));

        modelMapper.map(roomDto, room);
        room.setId(roomId);         // in case the id is not set in the request body, we set it here to make sure the room is updated instead of creating a new one

//        TODO: if price or inventory is updated, then update the inventory for this room
        room = roomRepository.save(room);

        return modelMapper.map(room, RoomDto.class);
    }
}
