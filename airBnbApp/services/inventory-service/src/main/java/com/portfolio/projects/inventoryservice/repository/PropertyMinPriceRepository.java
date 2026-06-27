package com.portfolio.projects.inventoryservice.repository;

import com.portfolio.projects.inventoryservice.entity.PropertyMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PropertyMinPriceRepository extends JpaRepository<PropertyMinPrice, Long> {

    // Removed the query that returned PropertyPriceDto directly because it relied on Property entity
    // In a microservices architecture, we'd query inventory independently or publish price events

    Optional<PropertyMinPrice> findByPropertyIdAndDate(Long propertyId, LocalDate date);
}
