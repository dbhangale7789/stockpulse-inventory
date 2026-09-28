package com.stockpulse.inventory.controller;

import com.stockpulse.inventory.service.CommerceStrategyService;
import com.stockpulse.inventory.service.StrategyRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/strategies")
public class AdminStrategyController {

    private final StrategyRegistry strategyRegistry;
    private final CommerceStrategyService commerceStrategyService;

    public AdminStrategyController(StrategyRegistry strategyRegistry, CommerceStrategyService commerceStrategyService) {
        this.strategyRegistry = strategyRegistry;
        this.commerceStrategyService = commerceStrategyService;
    }

    @GetMapping
    public Map<String, Object> listStrategies() {
        Map<String, Object> out = new HashMap<>();
        out.put("activePricingStrategy", commerceStrategyService.getPricingStrategyName());
        out.put("activeReorderStrategy", commerceStrategyService.getReorderStrategyName());
        out.put("availablePricingStrategies", strategyRegistry.getPricingStrategyNames());
        out.put("availableReorderStrategies", strategyRegistry.getReorderStrategyNames());
        return out;
    }

    @PatchMapping("/pricing")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPricingStrategy(@RequestBody Map<String, String> payload) {
        String name = payload.get("strategy");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("strategy is required");
        // validate
        strategyRegistry.getPricingStrategy(name);
        commerceStrategyService.setPricingStrategyName(name);
    }

    @PatchMapping("/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setReorderStrategy(@RequestBody Map<String, String> payload) {
        String name = payload.get("strategy");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("strategy is required");
        // validate
        strategyRegistry.getReorderStrategy(name);
        commerceStrategyService.setReorderStrategyName(name);
    }
}
