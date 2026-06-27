package com.portfolio.projects.propertyservice.repository;

import com.portfolio.projects.propertyservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
