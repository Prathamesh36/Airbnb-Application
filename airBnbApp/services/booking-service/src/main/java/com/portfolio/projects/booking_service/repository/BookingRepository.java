package com.portfolio.projects.booking_service.repository;

import com.portfolio.projects.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPropertyId(Long propertyId);

    List<Booking> findByPropertyIdAndCreatedAtBetween(Long propertyId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findByUserId(Long userId);

    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByBookingStatusInAndCreatedAtBefore(List<com.portfolio.projects.common.enums.BookingStatus> statuses, LocalDateTime time);
}
