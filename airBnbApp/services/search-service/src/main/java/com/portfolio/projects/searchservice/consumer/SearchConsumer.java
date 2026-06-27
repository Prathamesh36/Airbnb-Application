package com.portfolio.projects.searchservice.consumer;

import com.portfolio.projects.searchservice.entity.PropertyIndex;
import com.portfolio.projects.searchservice.repository.PropertyIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchConsumer {

    private final PropertyIndexRepository propertyIndexRepository;

    @KafkaListener(topics = "property-created-topic", groupId = "search-service-group")
    public void consumePropertyCreatedEvent(String message) {
        log.info("Consumed property-created-topic message: {}", message);
        // Implement property indexing logic here
    }

    @KafkaListener(topics = "inventory-updated-topic", groupId = "search-service-group")
    public void consumeInventoryUpdatedEvent(String message) {
        log.info("Consumed inventory-updated-topic message: {}", message);
        // Implement min price update logic here
    }
}
