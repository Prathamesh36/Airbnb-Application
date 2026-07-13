package com.portfolio.projects.propertyservice.service.impl;

import com.portfolio.projects.common.dto.RoomDto;
import com.portfolio.projects.propertyservice.entity.Property;
import com.portfolio.projects.propertyservice.entity.Room;
import com.portfolio.projects.common.exception.ResourceNotFoundException;
import com.portfolio.projects.propertyservice.exception.UnAuthorisedException;
import com.portfolio.projects.propertyservice.repository.PropertyRepository;
import com.portfolio.projects.propertyservice.repository.RoomRepository;

import com.portfolio.projects.common.dto.RoomCreatedEvent;
import com.portfolio.projects.propertyservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final PropertyRepository PropertyRepository;

    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private Long getLoggedInUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public RoomDto createNewRoom(Long propertyId, RoomDto roomDto) {
        log.info("Creating a new room in Property with ID: {}", propertyId);
        Property Property = PropertyRepository
                .findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+propertyId));

        Long userId = getLoggedInUserId();
        if(!userId.equals(Property.getOwnerId())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+propertyId);
        }

        Room room = modelMapper.map(roomDto, Room.class);
        room.setProperty(Property);
        room = roomRepository.save(room);

        if (Property.getActive()) {
            RoomCreatedEvent event = RoomCreatedEvent.builder()
                    .propertyId(Property.getId())
                    .roomId(room.getId())
                    .totalCount(room.getTotalCount())
                    .basePrice(room.getBasePrice())
                    .city(Property.getCity())
                    .build();
            kafkaTemplate.send("room-created-topic", event);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInProperty(Long propertyId) {
        log.info("Getting all rooms in Property with ID: {}", propertyId);
        Property Property = PropertyRepository
                .findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+propertyId));

        Long userId = getLoggedInUserId();
        if(!userId.equals(Property.getOwnerId())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+propertyId);
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

        Long userId = getLoggedInUserId();
        if(!userId.equals(room.getProperty().getOwnerId())) {
            throw new UnAuthorisedException("This user does not own this room with id: "+roomId);
        }

        // inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }

    @Override
    @Transactional
    public RoomDto updateRoomById(Long propertyId, Long roomId, RoomDto roomDto) {
        log.info("Updating the room with ID: {}", roomId);
        Property Property = PropertyRepository
                .findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+propertyId));

        Long userId = getLoggedInUserId();
        if(!userId.equals(Property.getOwnerId())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+propertyId);
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
