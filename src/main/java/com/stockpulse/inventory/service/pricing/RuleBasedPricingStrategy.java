package com.stockpulse.inventory.service.pricing;

import com.stockpulse.inventory.domain.ChangeDirection;
import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductContext;
import com.stockpulse.inventory.domain.TriggerReason;
import com.stockpulse.inventory.dto.PricingSuggestionResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RuleBasedPricingStrategy implements PricingStrategy {

    @Override
    public PricingSuggestionResponse evaluate(ProductContext context) {
        Product product = context.getProduct();
        BigDecimal currentPrice = product.getPrice();
        BigDecimal recommendedPrice = currentPrice;
        ChangeDirection direction = ChangeDirection.HOLD;
        String reasoning = "Current pricing is aligned with demand and stock conditions.";

        if (product.getStockQuantity() <= product.getReorderThreshold()) {
            recommendedPrice = currentPrice.multiply(new BigDecimal("1.10"));
            direction = ChangeDirection.INCREASE;
            reasoning = "Stock is at or below reorder threshold. Increase price to protect remaining inventory.";
        } else if (context.getDemandVelocity() > (context.getCategoryAverageDemandVelocity() * 2.0)) {
            recommendedPrice = currentPrice.multiply(new BigDecimal("1.05"));
            direction = ChangeDirection.INCREASE;
            reasoning = "Demand velocity is more than 2x the category average. A moderate increase is justified.";
        }

        if (context.getTriggerReason() == TriggerReason.MANUAL && product.getStockQuantity() > product.getReorderThreshold()) {
            reasoning = "Manual review requested. Pricing remains stable unless stock or demand indicates a stronger signal.";
            recommendedPrice = currentPrice;
            direction = ChangeDirection.HOLD;
        }

        return new PricingSuggestionResponse(
                null,
                product.getId(),
                currentPrice,
                recommendedPrice,
                direction,
                new BigDecimal("0.85"),
                reasoning,
                com.stockpulse.inventory.domain.SuggestionStatus.PENDING,
                context.getTriggerReason(),
                null,
                null
        );
    }

    @Override
    public String getName() {
        return "rule-based-pricing";
    }
}
