package com.portfolio.projects.airBnbApp.service.impl;

import com.portfolio.projects.airBnbApp.entity.Property;
import com.portfolio.projects.airBnbApp.entity.PropertyMinPrice;
import com.portfolio.projects.airBnbApp.entity.Inventory;
import com.portfolio.projects.airBnbApp.repository.PropertyMinPriceRepository;
import com.portfolio.projects.airBnbApp.repository.PropertyRepository;
import com.portfolio.projects.airBnbApp.repository.InventoryRepository;
import com.portfolio.projects.airBnbApp.strategy.PricingService;
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

    // Scheduler to update the inventory and PropertyMinPrice tables every hour

    private final PropertyRepository PropertyRepository;
    private final InventoryRepository inventoryRepository;
    private final PropertyMinPriceRepository PropertyMinPriceRepository;
    private final PricingService pricingService;

//    @Scheduled(cron = "*/5 * * * * *")
    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {
        int page = 0;
        int batchSize = 100;

        while(true) {
            Page<Property> PropertyPage = PropertyRepository.findAll(PageRequest.of(page, batchSize));
            if(PropertyPage.isEmpty()) {
                break;
            }
            PropertyPage.getContent().forEach(this::updatePropertyPrices);

            page++;
        }
    }

    private void updatePropertyPrices(Property Property) {
        log.info("Updating Property prices for Property ID: {}", Property.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByPropertyAndDateBetween(Property, startDate, endDate);

        updateInventoryPrices(inventoryList);

        updatePropertyMinPrice(Property, inventoryList, startDate, endDate);
    }

    private void updatePropertyMinPrice(Property Property, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        // Compute minimum price per day for the Property
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        // Prepare PropertyPrice entities in bulk
        List<PropertyMinPrice> PropertyPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            PropertyMinPrice PropertyPrice = PropertyMinPriceRepository.findByPropertyAndDate(Property, date)
                    .orElse(new PropertyMinPrice(Property, date));
            PropertyPrice.setPrice(price);
            PropertyPrices.add(PropertyPrice);
        });

        // Save all PropertyPrice entities in bulk
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
