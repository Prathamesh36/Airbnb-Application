package com.portfolio.projects.inventoryservice.service.impl;

import com.portfolio.projects.inventoryservice.entity.PropertyMinPrice;
import com.portfolio.projects.inventoryservice.entity.Inventory;
import com.portfolio.projects.inventoryservice.repository.PropertyMinPriceRepository;
import com.portfolio.projects.inventoryservice.repository.InventoryRepository;
import com.portfolio.projects.inventoryservice.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

    private final InventoryRepository inventoryRepository;
    private final PropertyMinPriceRepository PropertyMinPriceRepository;
    private final PricingService pricingService;

//    @Scheduled(cron = "*/5 * * * * *")
    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {
        int page = 0;
        int batchSize = 100;

        while(true) {
            Page<Long> propertyIdPage = inventoryRepository.findDistinctPropertyIds(PageRequest.of(page, batchSize));
            if(propertyIdPage.isEmpty()) {
                break;
            }
            propertyIdPage.getContent().forEach(this::updatePropertyPrices);

            page++;
        }
    }

    private void updatePropertyPrices(Long propertyId) {
        log.info("Updating Property prices for Property ID: {}", propertyId);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByPropertyIdAndDateBetween(propertyId, startDate, endDate);

        updateInventoryPrices(inventoryList);

        updatePropertyMinPrice(propertyId, inventoryList, startDate, endDate);
    }

    private void updatePropertyMinPrice(Long propertyId, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        List<PropertyMinPrice> PropertyPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            PropertyMinPrice PropertyPrice = PropertyMinPriceRepository.findByPropertyIdAndDate(propertyId, date)
                    .orElse(new PropertyMinPrice(propertyId, date));
            PropertyPrice.setPrice(price);
            PropertyPrices.add(PropertyPrice);
        });

        PropertyMinPriceRepository.saveAll(PropertyPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }
}
