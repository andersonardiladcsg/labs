# Cart Sanitization Pipeline

> This page documents the cart sanitization pipeline — a series of steps that run every
> time a cart is retrieved or modified to ensure data integrity before it is returned to the client.

## Pipeline Overview

The sanitization pipeline is orchestrated by
[`CartSanitizerService.java`](../src/main/java/com/example/services/CartSanitizerService.java).
Each sanitizer runs in strict sequence; the output of one feeds into the next.

<!-- AUTO-START -->

## Pipeline Execution Order

| Step | Sanitizer | Source | What it does |
|------|-----------|--------|--------------|
| 1 | DuplicateSanitizer | [`DuplicateSanitizer.java`](../src/main/java/com/example/sanitizers/DuplicateSanitizer.java) | Deduplicates cart items by SKU |
| 2 | PriceSanitizer | [`PriceSanitizer.java`](../src/main/java/com/example/sanitizers/PriceSanitizer.java) | Validates and corrects item pricing |
| 3 | InventorySanitizer | [`InventorySanitizer.java`](../src/main/java/com/example/sanitizers/InventorySanitizer.java) | Removes or adjusts items based on stock availability |
| 4 | QuantityLimitSanitizer | [`QuantityLimitSanitizer.java`](../src/main/java/com/example/sanitizers/QuantityLimitSanitizer.java) | Enforces per-item and cart-wide quantity limits |

---

## 1. DuplicateSanitizer

**Purpose:** Removes duplicate SKUs from the cart, keeping the first occurrence of each.

### What it does

- Uses a `LinkedHashMap` keyed by SKU to deduplicate while preserving insertion order
- Keeps the first occurrence of each SKU and silently drops any later duplicates
- Logs a warning whenever duplicates are removed

### Configuration

_None_

### Dependencies

_None_

---

## 2. PriceSanitizer

**Purpose:** Validates and corrects item pricing by fetching live prices and capping excessive discounts.

### What it does

- Looks up the current price for each item via `PricingService`
- Drops items whose price falls below the configured minimum
- Caps discounts at the configured maximum (default: 50%)
- Notifies the customer when a price is corrected or an item is removed

### Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `sanitizer.price.max-discount-percent` | `50.0` | Maximum discount percentage allowed |
| `sanitizer.price.min-price` | `0.01` | Items priced below this value are removed |

### Dependencies

| Service | Purpose |
|---------|---------|
| PricingService | Provides current prices by SKU |

---

## 3. InventorySanitizer

**Purpose:** Checks stock availability and adjusts or removes items that can't be fulfilled.

### What it does

- Queries `InventoryService` for available stock per SKU
- Removes items that are completely out of stock
- If quantity exceeds available stock and `autoAdjustQuantity` is enabled, reduces the quantity to match stock
- If quantity exceeds available stock and `autoAdjustQuantity` is disabled, removes the item entirely
- Sets an `inStock` flag on each remaining item
- Notifies the customer when quantities are adjusted or items are removed

### Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `sanitizer.inventory.auto-adjust-quantity` | `true` | When `true`, reduces quantity to match stock rather than removing the item |

### Dependencies

| Service | Purpose |
|---------|---------|
| InventoryService | Provides available stock counts by SKU |

---

## 4. QuantityLimitSanitizer

**Purpose:** Enforces per-item and cart-wide quantity limits after stock availability has been checked.

### What it does

- Clamps each item's quantity within the configured minimum and maximum bounds
- If a quantity exceeds the maximum, it is reduced and the customer is notified
- If a quantity is below the minimum, it is raised and the customer is notified
- If the cart contains more unique SKUs than the configured maximum, trailing items are removed until the cart is within the limit
- Notifies the customer when items are dropped due to cart size overflow

### Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `sanitizer.quantity-limit.max-per-item` | `10` | Maximum quantity allowed per line item |
| `sanitizer.quantity-limit.min-per-item` | `1` | Minimum quantity allowed per line item |
| `sanitizer.quantity-limit.max-cart-size` | `50` | Maximum number of distinct SKUs in the cart |

### Dependencies

_None_

<!-- AUTO-END -->

---

*Last updated: 2026-03-03 | Commit: 0c3b92aeba1020ba1d68f58413c8f6e45ac8bb7b*
