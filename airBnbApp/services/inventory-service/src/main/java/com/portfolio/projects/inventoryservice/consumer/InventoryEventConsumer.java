package com.portfolio.projects.inventoryservice.consumer;

import com.portfolio.projects.inventoryservice.dto.RoomCreatedEvent;
import com.portfolio.projects.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "room-created-topic", groupId = "inventory-service-group")
    public void consumeRoomCreatedEvent(RoomCreatedEvent event) {
        log.info("Received RoomCreatedEvent for Property ID: {} and Room ID: {}", event.getPropertyId(), event.getRoomId());
        try {
            inventoryService.initializeRoomForAYear(event);
            log.info("Successfully initialized inventory for Room ID: {}", event.getRoomId());
        } catch (Exception e) {
            log.error("Failed to initialize inventory for Room ID: {}", event.getRoomId(), e);
        }
    }
}
