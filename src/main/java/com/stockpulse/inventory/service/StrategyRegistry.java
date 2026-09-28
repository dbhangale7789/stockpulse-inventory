package com.stockpulse.inventory.service;

import com.stockpulse.inventory.service.pricing.PricingStrategy;
import com.stockpulse.inventory.service.reorder.ReorderStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StrategyRegistry {

    private final Map<String, PricingStrategy> pricingStrategies;
    private final Map<String, ReorderStrategy> reorderStrategies;

    public StrategyRegistry(List<PricingStrategy> pricingStrategies,
                           List<ReorderStrategy> reorderStrategies) {
        this.pricingStrategies = new ConcurrentHashMap<>();
        this.reorderStrategies = new ConcurrentHashMap<>();

        pricingStrategies.forEach(strategy -> this.pricingStrategies.put(strategy.getName(), strategy));
        reorderStrategies.forEach(strategy -> this.reorderStrategies.put(strategy.getName(), strategy));
    }

    public PricingStrategy getPricingStrategy(String name) {
        PricingStrategy selected = pricingStrategies.get(name);
        if (selected == null) {
            throw new IllegalArgumentException("Unknown pricing strategy: " + name);
        }
        return selected;
    }

    public ReorderStrategy getReorderStrategy(String name) {
        ReorderStrategy selected = reorderStrategies.get(name);
        if (selected == null) {
            throw new IllegalArgumentException("Unknown reorder strategy: " + name);
        }
        return selected;
    }

    public java.util.Set<String> getPricingStrategyNames() {
        return java.util.Collections.unmodifiableSet(pricingStrategies.keySet());
    }

    public java.util.Set<String> getReorderStrategyNames() {
        return java.util.Collections.unmodifiableSet(reorderStrategies.keySet());
    }
}
