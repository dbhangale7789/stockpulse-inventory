package com.stockpulse.inventory.repository;

import com.stockpulse.inventory.domain.PricingSuggestion;
import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.SuggestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingSuggestionRepository extends JpaRepository<PricingSuggestion, Long> {
    List<PricingSuggestion> findByProduct(Product product);
    List<PricingSuggestion> findByStatus(SuggestionStatus status);
}
