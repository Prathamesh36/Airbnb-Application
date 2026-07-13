package com.portfolio.projects.inventoryservice.service.impl;

import com.portfolio.projects.common.dto.*;
import com.portfolio.projects.inventoryservice.entity.Inventory;
import com.portfolio.projects.inventoryservice.exception.ResourceNotFoundException;
import com.portfolio.projects.inventoryservice.repository.PropertyMinPriceRepository;
import com.portfolio.projects.inventoryservice.repository.InventoryRepository;
import com.portfolio.projects.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import com.portfolio.projects.inventoryservice.strategy.PricingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{
    private final ModelMapper modelMapper;

    private final InventoryRepository inventoryRepository;
    private final PropertyMinPriceRepository PropertyMinPriceRepository;
    private final PricingService pricingService;

    @Override
    public void initializeRoomForAYear(RoomCreatedEvent room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for (; !today.isAfter(endDate); today=today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .propertyId(room.getPropertyId())
                    .roomId(room.getRoomId())
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getCity())
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
    @Transactional
    public void deleteAllInventories(Long roomId) {
        log.info("Deleting the inventories of room with id: {}", roomId);
        inventoryRepository.deleteByRoomId(roomId);
    }

    @Override
    public Page<PropertyPriceDto> searchPropertys(PropertySearchRequest PropertySearchRequest) {
        log.info("Searching Propertys for {} city, from {} to {}", PropertySearchRequest.getCity(), PropertySearchRequest.getStartDate(), PropertySearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(PropertySearchRequest.getPage(), PropertySearchRequest.getSize());
        long dateCount =
                ChronoUnit.DAYS.between(PropertySearchRequest.getStartDate(), PropertySearchRequest.getEndDate()) + 1;

        Page<Long> propertyIdsPage =
                inventoryRepository.findPropertyIdsWithAvailableInventory(PropertySearchRequest.getCity(),
                        PropertySearchRequest.getStartDate(), PropertySearchRequest.getEndDate(), PropertySearchRequest.getRoomsCount(),
                        dateCount, pageable);

        // Map Long -> PropertyPriceDto temporarily or just return null for now 
        // In a real microservice, we'd fetch property details from PropertyService via Feign Client
        return propertyIdsPage.map(id -> new PropertyPriceDto(id, BigDecimal.ZERO));
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        log.info("Getting All inventory by room for room with id: {}", roomId);
        
        return inventoryRepository.findByRoomIdOrderByDate(roomId).stream()
                .map((element) -> modelMapper.map(element,
                        InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Updating All inventory by room for room with id: {} between date range: {} - {}", roomId,
                updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate());

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate());

        inventoryRepository.updateInventory(roomId, updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(), updateInventoryRequestDto.getClosed(),
                updateInventoryRequestDto.getSurgeFactor());
    }

    @Override
    @Transactional
    public ReserveInventoryResponse reserveInventory(InventoryBookingDto bookingDto) {
        log.info("Reserving inventory for room {} from {} to {}", bookingDto.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                bookingDto.getRoomId(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getRoomsCount()
        );

        long daysCount = ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate()) + 1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available for the requested dates");
        }

        int updatedRows = inventoryRepository.initBooking(
                bookingDto.getRoomId(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getRoomsCount()
        );
        
        if (updatedRows != daysCount) {
            throw new IllegalStateException("Database rejected inventory reservation due to concurrent booking");
        }

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingDto.getRoomsCount()));

        return ReserveInventoryResponse.builder()
                .priceForOneRoom(priceForOneRoom)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    @Transactional
    public void confirmInventory(InventoryBookingDto bookingDto) {
        log.info("Confirming inventory for room {} from {} to {}", bookingDto.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        
        inventoryRepository.findAndLockReservedInventory(
                bookingDto.getRoomId(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getRoomsCount()
        );

        int updatedRows = inventoryRepository.confirmBooking(
                bookingDto.getRoomId(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getRoomsCount()
        );
        
        long daysCount = ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate()) + 1;
        if (updatedRows != daysCount) {
            throw new IllegalStateException("Database rejected inventory confirmation");
        }
    }

    @Override
    @Transactional
    public void releaseInventory(InventoryBookingDto bookingDto) {
        log.info("Releasing/cancelling inventory for room {} from {} to {}", bookingDto.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        
        inventoryRepository.findAndLockBookedInventory(
                bookingDto.getRoomId(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getRoomsCount()
        );

        int updatedRows = inventoryRepository.cancelBooking(
                bookingDto.getRoomId(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getRoomsCount()
        );
        
        long daysCount = ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate()) + 1;
        if (updatedRows != daysCount) {
            log.warn("Database updated 0 rows for cancelBooking. It may have already been cancelled.");
        }
    }

    @Override
    @Transactional
    public void unreserveInventory(InventoryBookingDto bookingDto) {
        log.info("Unreserving inventory (releasing from EXPIRED booking) for room {} from {} to {}", bookingDto.getRoomId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        
        inventoryRepository.findAndLockReservedInventory(
                bookingDto.getRoomId(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getRoomsCount()
        );

        int updatedRows = inventoryRepository.unreserveBooking(
                bookingDto.getRoomId(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getRoomsCount()
        );
        
        long daysCount = ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate()) + 1;
        if (updatedRows != daysCount) {
            log.warn("Database updated 0 rows for unreserveBooking. It may have already been unreserved.");
        }
    }
}
