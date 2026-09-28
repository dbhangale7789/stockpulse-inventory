package com.stockpulse.inventory.repository;

import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ReorderSuggestion;
import com.stockpulse.inventory.domain.SuggestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReorderSuggestionRepository extends JpaRepository<ReorderSuggestion, Long> {
    List<ReorderSuggestion> findByProduct(Product product);
    List<ReorderSuggestion> findByStatus(SuggestionStatus status);
}
