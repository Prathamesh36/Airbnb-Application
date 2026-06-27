package com.portfolio.projects.inventoryservice.strategy;

import com.portfolio.projects.inventoryservice.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getPrice().divide(inventory.getSurgeFactor(), java.math.RoundingMode.HALF_UP);
    }
}
