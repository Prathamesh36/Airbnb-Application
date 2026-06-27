package com.portfolio.projects.propertyservice.repository;

import com.portfolio.projects.propertyservice.entity.Property;
import com.portfolio.projects.propertyservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwner(User user);
}

