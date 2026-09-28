package com.stockpulse.inventory.dto;

import com.stockpulse.inventory.domain.ChangeDirection;
import com.stockpulse.inventory.domain.PricingSuggestion;
import com.stockpulse.inventory.domain.SuggestionStatus;
import com.stockpulse.inventory.domain.TriggerReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PricingSuggestionResponse(
        Long id,
        Long productId,
        BigDecimal currentPrice,
        BigDecimal recommendedPrice,
        ChangeDirection changeDirection,
        BigDecimal confidence,
        String reasoning,
        SuggestionStatus status,
        TriggerReason triggerReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PricingSuggestionResponse from(PricingSuggestion suggestion) {
        return new PricingSuggestionResponse(
                suggestion.getId(),
                suggestion.getProduct().getId(),
                suggestion.getCurrentPrice(),
                suggestion.getRecommendedPrice(),
                suggestion.getChangeDirection(),
                suggestion.getConfidence(),
                suggestion.getReasoning(),
                suggestion.getStatus(),
                suggestion.getTriggerReason(),
                suggestion.getCreatedAt(),
                suggestion.getUpdatedAt()
        );
    }
}
