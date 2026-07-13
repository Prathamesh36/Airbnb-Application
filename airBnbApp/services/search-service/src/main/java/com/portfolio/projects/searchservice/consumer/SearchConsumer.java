package com.portfolio.projects.searchservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.projects.common.dto.PropertySearchEvent;
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
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "property-created-topic", groupId = "search-service-group")
    public void consumePropertyCreatedEvent(String message) {
        log.info("Consumed property-created-topic message: {}", message);
        try {
            PropertySearchEvent event = objectMapper.readValue(message, PropertySearchEvent.class);
            PropertyIndex propertyIndex = new PropertyIndex();
            propertyIndex.setPropertyId(event.getPropertyId());
            propertyIndex.setName(event.getName());
            propertyIndex.setCity(event.getCity());
            propertyIndex.setActive(event.getActive());
            // Default minPrice, can be updated later by inventory events
            propertyIndex.setMinPrice(BigDecimal.ZERO);
            
            propertyIndexRepository.save(propertyIndex);
            log.info("Successfully indexed property: {}", event.getPropertyId());
        } catch (Exception e) {
            log.error("Error processing property search event", e);
        }
    }

    @KafkaListener(topics = "inventory-updated-topic", groupId = "search-service-group")
    public void consumeInventoryUpdatedEvent(String message) {
        log.info("Consumed inventory-updated-topic message: {}", message);
        // Implement min price update logic here
    }
}
