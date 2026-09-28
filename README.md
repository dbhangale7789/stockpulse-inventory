# StockPulse Inventory API

## Purpose
This project is a simplified Spring Boot inventory management application for tracking products, stock levels, and inventory decisions. It does not include storefront, cart, or payment flows.

## Technology stack
- Java 21
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- H2 in-memory database
- REST APIs

## Project structure
- `src/main/java/com/stockpulse/inventory`
  - `controller`
  - `service`
  - `repository`
  - `domain`
  - `dto`
  - `exception`
- `src/main/resources/application.properties`

## Run requirements
- Install JDK 21
- Install Maven
- Ensure Java and Maven are available in PATH

## Run project
1. Open terminal in the project root
2. Run:
   ```bash
   mvn clean install
   ```
3. Start the application:
   ```bash
   mvn spring-boot:run
   ```

## Default app URL
- `http://localhost:8080`

## Base API path
- `/api`

## H2 database console
- `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:stockpulse`
- Username: `sa`
- Password: blank

## Main configuration
The application configuration is in:
- `src/main/resources/application.properties`

### Important properties
- Server port: `8080`
- H2 database enabled
- JPA Hibernate auto-update enabled
- Strategy configuration values for pricing and reorder logic

## Product API
### 1) Create product
`POST /api/products`

Request JSON:
```json
{
  "sku": "SKU-100",
  "name": "Wireless Mouse",
  "category": "ELECTRONICS",
  "price": 29.99,
  "stockQuantity": 25,
  "reorderThreshold": 10
}
```

### 2) Get all products
`GET /api/products`

### 3) Get product by id
`GET /api/products/{id}`

### 4) Update product
`PATCH /api/products/{id}`

Example:
```json
{
  "name": "Wireless Mouse Pro",
  "price": 34.99,
  "stockQuantity": 18,
  "reorderThreshold": 12
}
```

### 5) Delete product
`DELETE /api/products/{id}`

### 6) Search/filter products
`GET /api/products?status=&category=`

Example:
```http
GET /api/products?status=LOW_STOCK&category=ELECTRONICS
```

## Order API
### 1) Create product order
`POST /api/products/{id}/orders`

Request JSON:
```json
{
  "quantity": 5
}
```

### Stock validation
- Quantity cannot exceed available stock
- If stock is insufficient, the API returns a validation error

## Suggestion API
### 1) Create pricing suggestion
`POST /api/products/{id}/suggest-pricing`

Request JSON:
```json
{
  "triggerReason": "MANUAL"
}
```

### 2) Create reorder suggestion
`POST /api/products/{id}/suggest-reorder`

Request JSON:
```json
{
  "triggerReason": "MANUAL"
}
```

### 3) Update pricing suggestion
`PATCH /api/pricing-suggestions/{id}`

Request JSON:
```json
{
  "status": "ACCEPTED",
  "decisionNotes": "Approved after review"
}
```

### 4) Update reorder suggestion
`PATCH /api/reorder-suggestions/{id}`

Request JSON:
```json
{
  "status": "REJECTED",
  "decisionNotes": "Stock is still healthy"
}
```

## Strategy switching API
Admin endpoints exist for runtime strategy switching:
- `GET /api/admin/strategies`
- `POST /api/admin/strategies/pricing/{strategyName}`
- `POST /api/admin/strategies/reorder/{strategyName}`

Example runtime switch:
```http
POST /api/admin/strategies/pricing/rule
POST /api/admin/strategies/reorder/rule
```

### Available strategy names
- `rule`
- `ai`

## Business rules
- Product must have a valid category
- Stock quantity cannot be negative
- Order quantity must be greater than zero
- Reorder threshold must be positive
- Product status is derived from stock level and state
- Suggestion flow supports accept/reject updates

## AI note
- AI provider integration is prepared but not active without a real key
- If no valid AI response is available, the app falls back safely to rule-based logic
- API key is expected from environment configuration and should never be committed to source control

## Example full test sequence
1. Create product
```http
POST /api/products
```
```json
{
  "sku": "SKU-100",
  "name": "Wireless Mouse",
  "category": "ELECTRONICS",
  "price": 29.99,
  "stockQuantity": 25,
  "reorderThreshold": 10
}
```

2. Create pricing suggestion
```http
POST /api/products/1/suggest-pricing
```
```json
{
  "triggerReason": "MANUAL"
}
```

3. Create reorder suggestion
```http
POST /api/products/1/suggest-reorder
```
```json
{
  "triggerReason": "INVENTORY_LOW"
}
```

4. Accept pricing suggestion
```http
PATCH /api/pricing-suggestions/1
```
```json
{
  "status": "ACCEPTED",
  "decisionNotes": "Approved"
}
```

5. Reject reorder suggestion
```http
PATCH /api/reorder-suggestions/1
```
```json
{
  "status": "REJECTED",
  "decisionNotes": "No reorder needed"
}
```

## Troubleshooting
- If the app does not start, verify JDK 21 is installed and Maven is configured
- If product APIs return not found, ensure the product was created first
- If a suggestion endpoint fails, confirm the product ID exists and the suggestion was created
- If AI fallback is triggered, it is expected behavior when no external AI key is configured

This completes the project documentation for the current working scope.
