package com.portfolio.projects.airBnbApp.service.impl;

import com.portfolio.projects.airBnbApp.dto.*;
import com.portfolio.projects.airBnbApp.entity.Property;
import com.portfolio.projects.airBnbApp.entity.Inventory;
import com.portfolio.projects.airBnbApp.entity.Room;
import com.portfolio.projects.airBnbApp.entity.User;
import com.portfolio.projects.airBnbApp.exception.ResourceNotFoundException;
import com.portfolio.projects.airBnbApp.repository.PropertyMinPriceRepository;
import com.portfolio.projects.airBnbApp.repository.InventoryRepository;
import com.portfolio.projects.airBnbApp.repository.RoomRepository;
import com.portfolio.projects.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.projects.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    private final InventoryRepository inventoryRepository;
    private final PropertyMinPriceRepository PropertyMinPriceRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for (; !today.isAfter(endDate); today=today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .Property(room.getProperty())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getProperty().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteAllInventories(Room room) {
        log.info("Deleting the inventories of room with id: {}", room.getId());
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<PropertyPriceDto> searchPropertys(PropertySearchRequest PropertySearchRequest) {
        log.info("Searching Propertys for {} city, from {} to {}", PropertySearchRequest.getCity(), PropertySearchRequest.getStartDate(), PropertySearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(PropertySearchRequest.getPage(), PropertySearchRequest.getSize());
        long dateCount =
                ChronoUnit.DAYS.between(PropertySearchRequest.getStartDate(), PropertySearchRequest.getEndDate()) + 1;

        // business logic - 90 days
        Page<PropertyPriceDto> PropertyPage =
                PropertyMinPriceRepository.findPropertysWithAvailableInventory(PropertySearchRequest.getCity(),
                        PropertySearchRequest.getStartDate(), PropertySearchRequest.getEndDate(), PropertySearchRequest.getRoomsCount(),
                        dateCount, pageable);

        return PropertyPage;
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        log.info("Getting All inventory by room for room with id: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: "+roomId));

        User user = getCurrentUser();
        if(!user.equals(room.getProperty().getOwner())) throw new AccessDeniedException("You are not the owner of room with id: "+roomId);

        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map((element) -> modelMapper.map(element,
                        InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Updating All inventory by room for room with id: {} between date range: {} - {}", roomId,
                updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate());

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: "+roomId));

        User user = getCurrentUser();
        if(!user.equals(room.getProperty().getOwner())) throw new AccessDeniedException("You are not the owner of room with id: "+roomId);

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate());

        inventoryRepository.updateInventory(roomId, updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(), updateInventoryRequestDto.getClosed(),
                updateInventoryRequestDto.getSurgeFactor());
    }
}

