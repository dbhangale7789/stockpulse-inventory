package com.stockpulse.inventory.service;

import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductContext;
import com.stockpulse.inventory.domain.TriggerReason;
import com.stockpulse.inventory.dto.PricingSuggestionResponse;
import com.stockpulse.inventory.dto.ReorderSuggestionResponse;
import com.stockpulse.inventory.service.pricing.PricingStrategy;
import com.stockpulse.inventory.service.reorder.ReorderStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommerceStrategyService {

    private final StrategyRegistry strategyRegistry;

    @Value("${commerce.pricing.strategy:rule-based-pricing}")
    private String pricingStrategyName;

    @Value("${commerce.reorder.strategy:rule-based-reorder}")
    private String reorderStrategyName;

    public CommerceStrategyService(StrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    @PostConstruct
    public void logSelectedStrategies() {
        System.out.println("Pricing strategy active: " + pricingStrategyName);
        System.out.println("Reorder strategy active: " + reorderStrategyName);
    }

    public PricingSuggestionResponse suggestPricing(Product product, TriggerReason triggerReason) {
        PricingStrategy strategy = strategyRegistry.getPricingStrategy(pricingStrategyName);
        ProductContext context = new ProductContext(
                product,
                product.getId(),
                java.math.BigDecimal.valueOf(75.0),
                15.0,
                8.0,
                triggerReason
        );
        return strategy.evaluate(context);
    }

    public ReorderSuggestionResponse suggestReorder(Product product, TriggerReason triggerReason) {
        ReorderStrategy strategy = strategyRegistry.getReorderStrategy(reorderStrategyName);
        ProductContext context = new ProductContext(
                product,
                product.getId(),
                java.math.BigDecimal.valueOf(75.0),
                15.0,
                8.0,
                triggerReason
        );
        return strategy.evaluate(context);
    }

    public void setPricingStrategyName(String pricingStrategyName) {
        this.pricingStrategyName = pricingStrategyName;
    }

    public void setReorderStrategyName(String reorderStrategyName) {
        this.reorderStrategyName = reorderStrategyName;
    }

    public String getPricingStrategyName() {
        return pricingStrategyName;
    }

    public String getReorderStrategyName() {
        return reorderStrategyName;
    }
}
