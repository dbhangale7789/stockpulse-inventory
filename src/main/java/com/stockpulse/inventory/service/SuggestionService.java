package com.stockpulse.inventory.service;

import com.stockpulse.inventory.domain.*;
import com.stockpulse.inventory.dto.PricingSuggestionResponse;
import com.stockpulse.inventory.dto.ReorderSuggestionResponse;
import com.stockpulse.inventory.repository.PricingSuggestionRepository;
import com.stockpulse.inventory.repository.ProductRepository;
import com.stockpulse.inventory.repository.ReorderSuggestionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SuggestionService {

    private final ProductRepository productRepository;
    private final PricingSuggestionRepository pricingSuggestionRepository;
    private final ReorderSuggestionRepository reorderSuggestionRepository;
    private final CommerceStrategyService commerceStrategyService;

    public SuggestionService(ProductRepository productRepository,
                            PricingSuggestionRepository pricingSuggestionRepository,
                            ReorderSuggestionRepository reorderSuggestionRepository,
                            CommerceStrategyService commerceStrategyService) {
        this.productRepository = productRepository;
        this.pricingSuggestionRepository = pricingSuggestionRepository;
        this.reorderSuggestionRepository = reorderSuggestionRepository;
        this.commerceStrategyService = commerceStrategyService;
    }

    public PricingSuggestionResponse createPricingSuggestion(Long productId, TriggerReason triggerReason) {
        Product product = getProduct(productId);
        PricingSuggestionResponse strategyResponse = commerceStrategyService.suggestPricing(product, triggerReason);

        PricingSuggestion suggestion = new PricingSuggestion(
                product,
                strategyResponse.currentPrice(),
                strategyResponse.recommendedPrice(),
                strategyResponse.changeDirection(),
                strategyResponse.confidence(),
                strategyResponse.reasoning(),
                strategyResponse.triggerReason()
        );

        return PricingSuggestionResponse.from(pricingSuggestionRepository.save(suggestion));
    }

    public ReorderSuggestionResponse createReorderSuggestion(Long productId, TriggerReason triggerReason) {
        Product product = getProduct(productId);
        ReorderSuggestionResponse strategyResponse = commerceStrategyService.suggestReorder(product, triggerReason);

        ReorderSuggestion suggestion = new ReorderSuggestion(
                product,
                strategyResponse.currentStock(),
                strategyResponse.recommendedQuantity(),
                strategyResponse.suggestedLeadTimeDays(),
                strategyResponse.confidence(),
                strategyResponse.reasoning(),
                strategyResponse.triggerReason()
        );

        return ReorderSuggestionResponse.from(reorderSuggestionRepository.save(suggestion));
    }

    @Transactional
    public PricingSuggestionResponse acceptPricingSuggestion(Long suggestionId) {
        PricingSuggestion suggestion = pricingSuggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Pricing suggestion not found with id: " + suggestionId));

        suggestion.setStatus(SuggestionStatus.ACCEPTED);
        suggestion.getProduct().setPrice(suggestion.getRecommendedPrice());
        suggestion.getProduct().setStatus(suggestion.getProduct().resolveStatus(suggestion.getProduct().getStockQuantity()));

        return PricingSuggestionResponse.from(pricingSuggestionRepository.save(suggestion));
    }

    @Transactional
    public ReorderSuggestionResponse acceptReorderSuggestion(Long suggestionId) {
        ReorderSuggestion suggestion = reorderSuggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Reorder suggestion not found with id: " + suggestionId));

        suggestion.setStatus(SuggestionStatus.ACCEPTED);
        int newStock = suggestion.getProduct().getStockQuantity() + suggestion.getRecommendedQuantity();
        suggestion.getProduct().setStockQuantity(newStock);
        suggestion.getProduct().setStatus(suggestion.getProduct().resolveStatus(newStock));

        return ReorderSuggestionResponse.from(reorderSuggestionRepository.save(suggestion));
    }

    @Transactional
    public PricingSuggestionResponse rejectPricingSuggestion(Long suggestionId) {
        PricingSuggestion suggestion = pricingSuggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Pricing suggestion not found with id: " + suggestionId));

        suggestion.setStatus(SuggestionStatus.REJECTED);
        return PricingSuggestionResponse.from(pricingSuggestionRepository.save(suggestion));
    }

    @Transactional
    public ReorderSuggestionResponse rejectReorderSuggestion(Long suggestionId) {
        ReorderSuggestion suggestion = reorderSuggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Reorder suggestion not found with id: " + suggestionId));

        suggestion.setStatus(SuggestionStatus.REJECTED);
        return ReorderSuggestionResponse.from(reorderSuggestionRepository.save(suggestion));
    }

    public List<PricingSuggestionResponse> getPricingSuggestionsForProduct(Long productId) {
        Product product = getProduct(productId);
        return pricingSuggestionRepository.findByProduct(product).stream()
                .map(PricingSuggestionResponse::from)
                .toList();
    }

    public List<ReorderSuggestionResponse> getReorderSuggestionsForProduct(Long productId) {
        Product product = getProduct(productId);
        return reorderSuggestionRepository.findByProduct(product).stream()
                .map(ReorderSuggestionResponse::from)
                .toList();
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }
}
