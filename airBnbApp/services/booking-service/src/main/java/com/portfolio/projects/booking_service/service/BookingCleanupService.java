package com.portfolio.projects.booking_service.service;

import com.portfolio.projects.booking_service.entity.Booking;
import com.portfolio.projects.booking_service.repository.BookingRepository;
import com.portfolio.projects.booking_service.client.InventoryClient;
import com.portfolio.projects.booking_service.client.dto.InventoryBookingDto;
import com.portfolio.projects.common.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupService {

    private final BookingRepository bookingRepository;
    private final InventoryClient inventoryClient;

    // Run every 10 seconds for testing
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void cleanupExpiredBookings() {
        // Expiration time is 1 minute for testing
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(1);
        
        List<BookingStatus> statuses = List.of(BookingStatus.RESERVED, BookingStatus.GUESTS_ADDED, BookingStatus.PAYMENTS_PENDING);
        List<Booking> expiredBookings = bookingRepository.findByBookingStatusInAndCreatedAtBefore(statuses, expirationTime);
        
        for (Booking booking : expiredBookings) {
            log.info("Cleaning up expired booking with ID: {}", booking.getId());
            booking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            
            // Release inventory
            InventoryBookingDto inventoryBookingDto = InventoryBookingDto.builder()
                    .roomId(booking.getRoomId())
                    .checkInDate(booking.getCheckInDate())
                    .checkOutDate(booking.getCheckOutDate())
                    .roomsCount(booking.getRoomsCount())
                    .build();
            try {
                inventoryClient.unreserveInventory(inventoryBookingDto);
                log.info("Successfully unreserved inventory for expired booking ID: {}", booking.getId());
            } catch (Exception e) {
                log.error("Failed to unreserve inventory for booking ID: {}", booking.getId(), e);
            }
        }
    }
}
