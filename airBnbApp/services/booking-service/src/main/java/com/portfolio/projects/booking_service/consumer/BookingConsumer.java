package com.portfolio.projects.booking_service.consumer;

import com.portfolio.projects.booking_service.entity.Booking;
import com.portfolio.projects.common.enums.BookingStatus;
import com.portfolio.projects.booking_service.exception.ResourceNotFoundException;
import com.portfolio.projects.booking_service.repository.BookingRepository;
import com.portfolio.projects.booking_service.client.InventoryClient;
import com.portfolio.projects.booking_service.client.dto.InventoryBookingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingConsumer {

    private final BookingRepository bookingRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    @KafkaListener(topics = "payment-completed-topic", groupId = "booking-service-group")
    public void consumePaymentCompletedEvent(Long bookingId) {
        log.info("Received payment-completed-topic for bookingId: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.PAYMENTS_PENDING) {
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            InventoryBookingDto inventoryBookingDto = InventoryBookingDto.builder()
                    .roomId(booking.getRoomId())
                    .checkInDate(booking.getCheckInDate())
                    .checkOutDate(booking.getCheckOutDate())
                    .roomsCount(booking.getRoomsCount())
                    .build();
            inventoryClient.confirmInventory(inventoryBookingDto);

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        } else if (booking.getBookingStatus() == BookingStatus.EXPIRED || booking.getBookingStatus() == BookingStatus.CANCELLED) {
            log.info("Booking ID: {} is EXPIRED. Attempting to re-reserve inventory...", bookingId);
            InventoryBookingDto inventoryBookingDto = InventoryBookingDto.builder()
                    .roomId(booking.getRoomId())
                    .checkInDate(booking.getCheckInDate())
                    .checkOutDate(booking.getCheckOutDate())
                    .roomsCount(booking.getRoomsCount())
                    .build();
            try {
                inventoryClient.reserveInventory(inventoryBookingDto);
                log.info("Successfully re-reserved inventory for Booking ID: {}", bookingId);

                booking.setBookingStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
                inventoryClient.confirmInventory(inventoryBookingDto);
                log.info("Successfully confirmed the late booking for Booking ID: {}", booking.getId());
            } catch (Exception e) {
                log.error("Failed to re-reserve inventory for Booking ID: {}. Triggering refund.", bookingId, e);
                booking.setBookingStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
                try {
                    kafkaTemplate.send("refund-booking-topic", bookingId).get();
                    log.info("Successfully published refund event to Kafka for bookingId: {}", bookingId);
                } catch (Exception kafkaEx) {
                    log.error("CRITICAL: Failed to publish refund event to Kafka for bookingId: {}. Manual refund required!", bookingId, kafkaEx);
                }
            }
        } else {
            log.warn("Booking with ID: {} is in state {} which is not valid for payment completion", bookingId, booking.getBookingStatus());
        }
    }
}
