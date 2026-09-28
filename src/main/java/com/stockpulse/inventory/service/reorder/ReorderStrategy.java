package com.stockpulse.inventory.service.reorder;

import com.stockpulse.inventory.domain.ProductContext;
import com.stockpulse.inventory.dto.ReorderSuggestionResponse;

public interface ReorderStrategy {
    ReorderSuggestionResponse evaluate(ProductContext context);
    String getName();
}
