package com.portfolio.projects.booking_service.consumer;

import com.portfolio.projects.booking_service.entity.Booking;
import com.portfolio.projects.booking_service.entity.enums.BookingStatus;
import com.portfolio.projects.booking_service.exception.ResourceNotFoundException;
import com.portfolio.projects.booking_service.repository.BookingRepository;
import com.portfolio.projects.booking_service.client.InventoryClient;
import com.portfolio.projects.booking_service.client.dto.InventoryBookingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingConsumer {

    private final BookingRepository bookingRepository;
    private final InventoryClient inventoryClient;

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
        } else {
            log.warn("Booking with ID: {} is not in PAYMENTS_PENDING state", bookingId);
        }
    }
}
