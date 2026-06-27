package com.portfolio.projects.searchservice.controller;

import com.portfolio.projects.searchservice.entity.PropertyIndex;
import com.portfolio.projects.searchservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<PropertyIndex>> searchProperties(@RequestParam String city) {
        return ResponseEntity.ok(searchService.searchProperties(city));
    }
}
