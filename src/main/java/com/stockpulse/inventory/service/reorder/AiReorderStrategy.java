package com.stockpulse.inventory.service.reorder;

import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductContext;
import com.stockpulse.inventory.domain.SuggestionStatus;
import com.stockpulse.inventory.dto.ReorderSuggestionResponse;
import com.stockpulse.inventory.service.ai.AiResponseValidator;
import com.stockpulse.inventory.service.ai.LlmClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class AiReorderStrategy implements ReorderStrategy {

    private final LlmClient llmClient;

    public AiReorderStrategy(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public ReorderSuggestionResponse evaluate(ProductContext context) {
        Product product = context.getProduct();
        try {
            String prompt = buildPrompt(product, context);
            String json = llmClient.call(prompt);
            Map<String, Object> payload = llmClient.parseJsonMap(json);

            Integer recommendedQuantity = Integer.valueOf(String.valueOf(payload.getOrDefault("recommendedQuantity", 1)));
            BigDecimal confidence = new BigDecimal(String.valueOf(payload.getOrDefault("confidence", "0.70")));
            String reasoning = String.valueOf(payload.getOrDefault("reasoning", "AI reorder recommendation generated."));

            if (!AiResponseValidator.isValidConfidence(confidence) || !AiResponseValidator.isValidQuantity(recommendedQuantity)) {
                throw new IllegalArgumentException("AI reorder response invalid");
            }

            return new ReorderSuggestionResponse(
                    null,
                    product.getId(),
                    product.getStockQuantity(),
                    recommendedQuantity,
                    7,
                    confidence,
                    reasoning,
                    SuggestionStatus.PENDING,
                    context.getTriggerReason(),
                    null,
                    null
            );
        } catch (Exception ex) {
            return new RuleBasedReorderStrategy().evaluate(context);
        }
    }

    private String buildPrompt(Product product, ProductContext context) {
        return "You are a replenishment advisor for an online store. " +
                "Product: " + product.getName() + ", Category: " + product.getCategory() + ", " +
                "Current stock: " + product.getStockQuantity() + ", Reorder threshold: " + product.getReorderThreshold() + ", " +
                "Demand velocity: " + context.getDemandVelocity() + ", Category average velocity: " + context.getCategoryAverageDemandVelocity() + ", " +
                "Trigger reason: " + context.getTriggerReason() + ". " +
                "Return JSON only with fields: recommendedQuantity, confidence, reasoning.";
    }

    @Override
    public String getName() {
        return "ai-reorder";
    }
}
