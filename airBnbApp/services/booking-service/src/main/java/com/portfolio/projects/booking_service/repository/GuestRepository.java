package com.portfolio.projects.booking_service.repository;

import com.portfolio.projects.booking_service.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
