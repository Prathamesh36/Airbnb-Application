package com.portfolio.projects.airBnbApp.strategy;

import com.portfolio.projects.airBnbApp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
