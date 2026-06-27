package com.portfolio.projects.inventoryservice.strategy;

import com.portfolio.projects.inventoryservice.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
