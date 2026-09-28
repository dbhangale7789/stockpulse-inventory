package com.stockpulse.inventory.service.pricing;

import com.stockpulse.inventory.domain.ChangeDirection;
import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductContext;
import com.stockpulse.inventory.domain.SuggestionStatus;
import com.stockpulse.inventory.dto.PricingSuggestionResponse;
import com.stockpulse.inventory.service.ai.AiResponseValidator;
import com.stockpulse.inventory.service.ai.LlmClient;
import com.stockpulse.inventory.service.pricing.PricingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class AiPricingStrategy implements PricingStrategy {

    private final LlmClient llmClient;

    public AiPricingStrategy(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public PricingSuggestionResponse evaluate(ProductContext context) {
        Product product = context.getProduct();
        try {
            String prompt = buildPrompt(product, context);
            String json = llmClient.call(prompt);
            Map<String, Object> payload = llmClient.parseJsonMap(json);

            BigDecimal recommendedPrice = new BigDecimal(String.valueOf(payload.getOrDefault("recommendedPrice", product.getPrice())));
            BigDecimal confidence = new BigDecimal(String.valueOf(payload.getOrDefault("confidence", "0.70")));
            String reason = String.valueOf(payload.getOrDefault("reasoning", "AI pricing recommendation generated."));
            String directionValue = String.valueOf(payload.getOrDefault("changeDirection", "HOLD")).toUpperCase();
            ChangeDirection direction = ChangeDirection.valueOf(directionValue);

            if (!AiResponseValidator.isValidConfidence(confidence) || !AiResponseValidator.isValidPrice(recommendedPrice, product.getPrice())) {
                throw new IllegalArgumentException("AI pricing response invalid");
            }

            return new PricingSuggestionResponse(
                    null,
                    product.getId(),
                    product.getPrice(),
                    recommendedPrice,
                    direction,
                    confidence,
                    reason,
                    SuggestionStatus.PENDING,
                    context.getTriggerReason(),
                    null,
                    null
            );
        } catch (Exception ex) {
            return new RuleBasedPricingStrategy().evaluate(context);
        }
    }

    private String buildPrompt(Product product, ProductContext context) {
        return "You are a pricing advisor for an online store. " +
                "Product: " + product.getName() + ", Category: " + product.getCategory() + ", " +
                "Current price: " + product.getPrice() + ", Stock: " + product.getStockQuantity() + ", Reorder threshold: " + product.getReorderThreshold() + ", " +
                "Demand velocity: " + context.getDemandVelocity() + ", Category average velocity: " + context.getCategoryAverageDemandVelocity() + ", " +
                "Trigger reason: " + context.getTriggerReason() + ". " +
                "Return JSON only with fields: recommendedPrice, changeDirection, confidence, reasoning.";
    }

    @Override
    public String getName() {
        return "ai-pricing";
    }
}
