package com.stockpulse.inventory.service.pricing;

import com.stockpulse.inventory.domain.ProductContext;
import com.stockpulse.inventory.dto.PricingSuggestionResponse;

public interface PricingStrategy {
    PricingSuggestionResponse evaluate(ProductContext context);
    String getName();
}
