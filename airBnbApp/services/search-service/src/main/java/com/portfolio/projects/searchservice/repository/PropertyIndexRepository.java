package com.portfolio.projects.searchservice.repository;

import com.portfolio.projects.searchservice.entity.PropertyIndex;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyIndexRepository extends CrudRepository<PropertyIndex, Long> {
    List<PropertyIndex> findByCityIgnoreCase(String city);
}
