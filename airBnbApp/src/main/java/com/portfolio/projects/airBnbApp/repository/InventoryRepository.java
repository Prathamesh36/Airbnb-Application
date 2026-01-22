package com.portfolio.projects.airBnbApp.repository;

import com.portfolio.projects.airBnbApp.entity.Inventory;
import com.portfolio.projects.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);
}
