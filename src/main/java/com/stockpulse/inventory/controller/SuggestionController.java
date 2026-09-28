package com.stockpulse.inventory.controller;

import com.stockpulse.inventory.domain.TriggerReason;
import com.stockpulse.inventory.dto.PricingSuggestionResponse;
import com.stockpulse.inventory.dto.ReorderSuggestionResponse;
import com.stockpulse.inventory.service.SuggestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @PostMapping("/products/{id}/suggest-pricing")
    @ResponseStatus(HttpStatus.CREATED)
    public PricingSuggestionResponse createPricingSuggestion(
            @PathVariable("id") Long productId,
            @RequestBody(required = false) Map<String, String> payload) {
        TriggerReason triggerReason = TriggerReason.MANUAL;
        if (payload != null && payload.get("triggerReason") != null) {
            triggerReason = TriggerReason.valueOf(payload.get("triggerReason"));
        }
        return suggestionService.createPricingSuggestion(productId, triggerReason);
    }

    @PostMapping("/products/{id}/suggest-reorder")
    @ResponseStatus(HttpStatus.CREATED)
    public ReorderSuggestionResponse createReorderSuggestion(
            @PathVariable("id") Long productId,
            @RequestBody(required = false) Map<String, String> payload) {
        TriggerReason triggerReason = TriggerReason.MANUAL;
        if (payload != null && payload.get("triggerReason") != null) {
            triggerReason = TriggerReason.valueOf(payload.get("triggerReason"));
        }
        return suggestionService.createReorderSuggestion(productId, triggerReason);
    }

    @PatchMapping("/pricing-suggestions/{id}")
    public PricingSuggestionResponse updatePricingSuggestion(@PathVariable("id") Long suggestionId,
                                                            @RequestBody Map<String, String> payload) {
        String action = payload.getOrDefault("status", "").toUpperCase();
        if ("ACCEPTED".equals(action)) {
            return suggestionService.acceptPricingSuggestion(suggestionId);
        }
        if ("REJECTED".equals(action)) {
            return suggestionService.rejectPricingSuggestion(suggestionId);
        }
        throw new IllegalArgumentException("Status must be ACCEPTED or REJECTED");
    }

    @PatchMapping("/reorder-suggestions/{id}")
    public ReorderSuggestionResponse updateReorderSuggestion(@PathVariable("id") Long suggestionId,
                                                            @RequestBody Map<String, String> payload) {
        String action = payload.getOrDefault("status", "").toUpperCase();
        if ("ACCEPTED".equals(action)) {
            return suggestionService.acceptReorderSuggestion(suggestionId);
        }
        if ("REJECTED".equals(action)) {
            return suggestionService.rejectReorderSuggestion(suggestionId);
        }
        throw new IllegalArgumentException("Status must be ACCEPTED or REJECTED");
    }
}
