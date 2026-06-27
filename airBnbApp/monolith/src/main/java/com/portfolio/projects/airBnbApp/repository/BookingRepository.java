package com.portfolio.projects.airBnbApp.repository;


import com.portfolio.projects.airBnbApp.entity.Booking;
import com.portfolio.projects.airBnbApp.entity.Property;
import com.portfolio.projects.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByProperty(Property Property);

    List<Booking> findByPropertyAndCreatedAtBetween(Property Property, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findByUser(User user);
}

