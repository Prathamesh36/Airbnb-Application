package com.portfolio.projects.airBnbApp.repository;

import com.portfolio.projects.airBnbApp.entity.Hotel;
import com.portfolio.projects.airBnbApp.entity.Inventory;
import com.portfolio.projects.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);

    //
    @Query("""
             SELECT DISTINCT i.hotel
             FROM Inventory i
             WHERE i.city = :city
                AND i.date BETWEEN :startDate AND :emdDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount) >= :roomsCount
                GROUP BY i.hotel, i.room
                HAVING COUNT(i.date) = :dateCount
            """)
    Page<Hotel> findHotelsWithAvailableInventory(@Param("city") String city,
                                                 @Param("startDate")LocalDate startDate,
                                                 @Param("emdDate")LocalDate endDate,
                                                 @Param("roomsCount")Integer roomsCount,
                                                 @Param("dateCount") Long dateCount,
                                                 Pageable pageable
                                                 );
}
