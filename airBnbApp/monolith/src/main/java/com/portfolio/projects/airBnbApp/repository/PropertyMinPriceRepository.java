package com.portfolio.projects.airBnbApp.repository;

import com.portfolio.projects.airBnbApp.entity.Property;
import com.portfolio.projects.airBnbApp.entity.PropertyMinPrice;
import com.portfolio.projects.airBnbApp.dto.PropertyPriceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PropertyMinPriceRepository extends JpaRepository<PropertyMinPrice, Long> {

    @Query("""
            SELECT new com.portfolio.projects.airBnbApp.dto.PropertyPriceDto(i.Property, AVG(i.price))
            FROM PropertyMinPrice i
            WHERE i.Property.city = :city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.Property.active = true
           GROUP BY i.Property
           """)
    Page<PropertyPriceDto> findPropertysWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    Optional<PropertyMinPrice> findByPropertyAndDate(Property Property, LocalDate date);
}
