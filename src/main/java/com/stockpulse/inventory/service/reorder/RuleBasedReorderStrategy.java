package com.stockpulse.inventory.service.reorder;

import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductContext;
import com.stockpulse.inventory.domain.TriggerReason;
import com.stockpulse.inventory.dto.ReorderSuggestionResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RuleBasedReorderStrategy implements ReorderStrategy {

    @Override
    public ReorderSuggestionResponse evaluate(ProductContext context) {
        Product product = context.getProduct();
        int recommendedQuantity = Math.max(1, (product.getReorderThreshold() * 3) - product.getStockQuantity());
        String reasoning = "Restock to bring inventory back above the reorder threshold.";

        if (context.getTriggerReason() == TriggerReason.DEMAND_SPIKE) {
            recommendedQuantity = Math.max(1, recommendedQuantity + 5);
            reasoning = "Demand spike is above the category norm. Recommend a larger order to avoid stockouts.";
        }

        return new ReorderSuggestionResponse(
                null,
                product.getId(),
                product.getStockQuantity(),
                recommendedQuantity,
                7,
                new BigDecimal("0.88"),
                reasoning,
                com.stockpulse.inventory.domain.SuggestionStatus.PENDING,
                context.getTriggerReason(),
                null,
                null
        );
    }

    @Override
    public String getName() {
        return "rule-based-reorder";
    }
}
