package com.portfolio.projects.propertyservice.service.impl;

import com.portfolio.projects.common.dto.PropertyDto;
import com.portfolio.projects.common.dto.PropertyInfoDto;
import com.portfolio.projects.common.dto.PropertyInfoRequestDto;
import com.portfolio.projects.common.dto.RoomDto;
import com.portfolio.projects.propertyservice.entity.Property;
import com.portfolio.projects.propertyservice.entity.Room;
import com.portfolio.projects.propertyservice.entity.User;
import com.portfolio.projects.propertyservice.exception.ResourceNotFoundException;
import com.portfolio.projects.propertyservice.exception.UnAuthorisedException;
import com.portfolio.projects.propertyservice.repository.PropertyRepository;
import com.portfolio.projects.propertyservice.repository.RoomRepository;
import com.portfolio.projects.propertyservice.service.PropertyService;

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
@Slf4j
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService{

    private final PropertyRepository PropertyRepository;
    private final ModelMapper modelMapper;

    private final RoomRepository roomRepository;

    @Override
    public PropertyDto createNewProperty(PropertyDto PropertyDto) {
        log.info("Creating a new Property with name: {}", PropertyDto.getName());
        Property Property = modelMapper.map(PropertyDto, Property.class);
        Property.setActive(false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Property.setOwner(user);

        Property = PropertyRepository.save(Property);
        log.info("Created a new Property with ID: {}", PropertyDto.getId());
        return modelMapper.map(Property, PropertyDto.class);
    }

    @Override
    public PropertyDto getPropertyById(Long id) {
        log.info("Getting the Property with ID: {}", id);
        Property Property = PropertyRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(Property.getOwner())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+id);
        }

        return modelMapper.map(Property, PropertyDto.class);
    }

    @Override
    public PropertyDto updatePropertyById(Long id, PropertyDto PropertyDto) {
        log.info("Updating the Property with ID: {}", id);
        Property Property = PropertyRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+id));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(Property.getOwner())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+id);
        }

        modelMapper.map(PropertyDto, Property);
        Property.setId(id);
        Property = PropertyRepository.save(Property);
        return modelMapper.map(Property, PropertyDto.class);
    }

    @Override
    @Transactional
    public void deletePropertyById(Long id) {
        Property Property = PropertyRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+id));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(Property.getOwner())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+id);
        }


        for(Room room: Property.getRooms()) {
            // inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        PropertyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateProperty(Long PropertyId) {
        log.info("Activating the Property with ID: {}", PropertyId);
        Property Property = PropertyRepository
                .findById(PropertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+PropertyId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(Property.getOwner())) {
            throw new UnAuthorisedException("This user does not own this Property with id: "+PropertyId);
        }

        Property.setActive(true);

        // assuming only do it once
        for(Room room: Property.getRooms()) {
            // inventoryService.initializeRoomForAYear(room);
        }
    }

    //    public method
    @Override
    public PropertyInfoDto getPropertyInfoById(Long PropertyId) {
        Property Property = PropertyRepository
                .findById(PropertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: "+PropertyId));

        List<RoomDto> rooms = Property.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();

        return new PropertyInfoDto(modelMapper.map(Property, PropertyDto.class), rooms);
    }

    @Override
    public List<PropertyDto> getAllPropertys() {
        User user = getCurrentUser();
        log.info("Getting all Propertys for the admin user with ID: {}", user.getId());
        List<Property> Propertys = PropertyRepository.findByOwner(user);

        return Propertys
                .stream()
                .map((element) -> modelMapper.map(element, PropertyDto.class))
                .collect(Collectors.toList());
    }


}
