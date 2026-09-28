# ADR: Split commerce strategy design with runtime strategy switching

Context: The inventory app needs pricing and reorder recommendations that can evolve independently, while both HTTP endpoints and async suggestion triggers reuse the same decision logic without code duplication.

Decision: We use a split design pattern with separate `PricingStrategy` and `ReorderStrategy` interfaces. This keeps pricing and replenishment concerns independent, makes each strategy easier to test and extend, and avoids coupling rule-based pricing logic to reorder logic.

Tradeoff: The system will need a small registry/dispatcher to call the correct pricing and reorder strategies together, but this flexibility is worth it because future offerings like AI pricing, competitor-aware pricing, or supplier-based replenishment can be added without changing the application flow.
