package com.stockpulse.inventory.dto;

import com.stockpulse.inventory.domain.ReorderSuggestion;
import com.stockpulse.inventory.domain.SuggestionStatus;
import com.stockpulse.inventory.domain.TriggerReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReorderSuggestionResponse(
        Long id,
        Long productId,
        Integer currentStock,
        Integer recommendedQuantity,
        Integer suggestedLeadTimeDays,
        BigDecimal confidence,
        String reasoning,
        SuggestionStatus status,
        TriggerReason triggerReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReorderSuggestionResponse from(ReorderSuggestion suggestion) {
        return new ReorderSuggestionResponse(
                suggestion.getId(),
                suggestion.getProduct().getId(),
                suggestion.getCurrentStock(),
                suggestion.getRecommendedQuantity(),
                suggestion.getSuggestedLeadTimeDays(),
                suggestion.getConfidence(),
                suggestion.getReasoning(),
                suggestion.getStatus(),
                suggestion.getTriggerReason(),
                suggestion.getCreatedAt(),
                suggestion.getUpdatedAt()
        );
    }
}
