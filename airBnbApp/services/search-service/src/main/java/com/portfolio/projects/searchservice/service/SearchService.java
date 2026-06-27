package com.portfolio.projects.searchservice.service;

import com.portfolio.projects.searchservice.entity.PropertyIndex;
import com.portfolio.projects.searchservice.repository.PropertyIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final PropertyIndexRepository propertyIndexRepository;

    public List<PropertyIndex> searchProperties(String city) {
        log.info("Searching properties for city: {}", city);
        return propertyIndexRepository.findByCityIgnoreCase(city);
    }
}
