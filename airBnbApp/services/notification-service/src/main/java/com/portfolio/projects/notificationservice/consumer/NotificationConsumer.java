package com.portfolio.projects.notificationservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class NotificationConsumer {

    @KafkaListener(topics = "payment-completed-topic", groupId = "notification-group")
    public void handlePaymentCompletedEvent(Map<String, Object> event) {
        log.info("==================================================");
        log.info("📧 MOCK EMAIL SENT: Payment completed successfully!");
        log.info("Event details: {}", event);
        log.info("==================================================");
    }
}
