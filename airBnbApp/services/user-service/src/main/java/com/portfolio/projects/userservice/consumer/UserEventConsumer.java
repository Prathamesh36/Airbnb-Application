package com.portfolio.projects.userservice.consumer;

import com.portfolio.projects.common.event.UserCreatedEvent;
import com.portfolio.projects.userservice.entity.User;
import com.portfolio.projects.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserRepository userRepository;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @KafkaListener(topics = "user-created-topic", groupId = "user-service-group")
    public void consumeUserCreatedEvent(String message) {
        try {
            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);
            log.info("Received UserCreatedEvent for user ID: {}", event.getId());

            User user = new User();
            user.setId(event.getId());
            user.setEmail(event.getEmail());
            user.setName(event.getName());
            user.setDateOfBirth(event.getDateOfBirth());
            user.setGender(event.getGender());
            user.setRoles(event.getRoles());
            user.setPassword("SYNCED_FROM_AUTH_SERVICE");

            userRepository.save(user);
            log.info("Successfully synchronized user ID: {} into user_service_db", event.getId());
        } catch (Exception e) {
            log.error("Failed to process user-created-topic message", e);
        }
    }
}
