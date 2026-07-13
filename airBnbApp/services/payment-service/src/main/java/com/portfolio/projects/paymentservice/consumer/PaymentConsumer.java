package com.portfolio.projects.paymentservice.consumer;

import com.portfolio.projects.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "refund-booking-topic", groupId = "payment-service-group")
    public void consumeRefundBookingEvent(Long bookingId) {
        log.info("Received refund-booking-topic for bookingId: {}", bookingId);
        try {
            paymentService.refundPayment(bookingId);
            log.info("Successfully initiated refund for bookingId: {}", bookingId);
        } catch (Exception e) {
            log.error("Failed to refund payment for bookingId: {}", bookingId, e);
        }
    }
}
