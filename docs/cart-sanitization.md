# Cart Sanitization Pipeline

> Reference documentation for the cart sanitization pipeline.
> The pipeline runs every time a cart is retrieved or modified, ensuring data integrity
> before the cart is returned to the client.

## Pipeline Overview

The sanitization pipeline is orchestrated by
[`CartSanitizerService.java`](../src/main/java/com/example/services/CartSanitizerService.java).
Each sanitizer runs in strict sequence; the output of one feeds into the next.

<!-- AUTO-START -->

## Pipeline Execution Order

| # | Sanitizer | Source File | Purpose |
|---|-----------|-------------|---------|
| 1 | DuplicateSanitizer | [`DuplicateSanitizer.java`](../src/main/java/com/example/sanitizers/DuplicateSanitizer.java) | Remove duplicate SKUs |
| 2 | PriceSanitizer | [`PriceSanitizer.java`](../src/main/java/com/example/sanitizers/PriceSanitizer.java) | Validate and correct pricing |
| 3 | InventorySanitizer | [`InventorySanitizer.java`](../src/main/java/com/example/sanitizers/InventorySanitizer.java) | Check stock availability |
| 4 | QuantityLimitSanitizer | [`QuantityLimitSanitizer.java`](../src/main/java/com/example/sanitizers/QuantityLimitSanitizer.java) | Enforce per-item and cart-wide quantity limits |

---

## 1. DuplicateSanitizer

**Purpose:** Removes duplicate SKUs from the cart, keeping the first occurrence of each.

### Behavior / Key Operations

- Iterates over cart items using a `LinkedHashMap` keyed by SKU to preserve insertion order
- Keeps the first occurrence of each SKU, discards subsequent duplicates
- Logs a warning when duplicates are removed

### Configuration Properties

_None_

### External Dependencies

_None_

---

## 2. PriceSanitizer

**Purpose:** Validates and corrects pricing for cart items by fetching current prices and applying discount caps.

### Behavior / Key Operations

- Fetches current price from PricingService for each SKU
- Removes items with price below the minimum threshold
- Caps discounts at a configurable maximum percentage (default: 50%)
- Adds user-facing messages when prices are corrected or items removed

### Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `sanitizer.price.max-discount-percent` | `50.0` | Maximum discount percentage allowed |
| `sanitizer.price.min-price` | `0.01` | Minimum valid price; items below this are removed |

### External Dependencies

| Service | Purpose |
|---------|---------|
| PricingService | Fetch current prices by SKU |

---

## 3. InventorySanitizer

**Purpose:** Checks stock availability and adjusts quantities or removes out-of-stock items.

### Behavior / Key Operations

- Calls InventoryService to check available stock per SKU
- Removes items with zero available stock
- When quantity exceeds stock and `autoAdjustQuantity` is enabled: adjusts quantity down to available stock
- When quantity exceeds stock and `autoAdjustQuantity` is disabled: removes the item
- Sets `inStock` flag on each item
- Adds user-facing messages when quantities are adjusted or items removed

### Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `sanitizer.inventory.auto-adjust-quantity` | `true` | When true, reduces quantity to match stock instead of removing the item |

### External Dependencies

| Service | Purpose |
|---------|---------|
| InventoryService | Check available stock by SKU |

---

## 4. QuantityLimitSanitizer

**Purpose:** Enforces per-item and cart-wide quantity limits after stock availability has been checked.

### Behavior / Key Operations

- Clamps each item's quantity to the configured `[minQuantityPerItem, maxQuantityPerItem]` range
- If an item's quantity exceeds the maximum, reduces it to the maximum and adds a user-facing message
- If an item's quantity is below the minimum, raises it to the minimum and adds a user-facing message
- If the total number of unique SKUs in the cart exceeds `maxCartSize`, removes items from the end of the list until within the limit
- Adds a user-facing message when items are removed due to cart size overflow

### Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `sanitizer.quantity-limit.max-per-item` | `10` | Maximum quantity allowed per item |
| `sanitizer.quantity-limit.min-per-item` | `1` | Minimum quantity allowed per item |
| `sanitizer.quantity-limit.max-cart-size` | `50` | Maximum number of unique SKUs in the cart |

### External Dependencies

_None_

<!-- AUTO-END -->

---

*Last updated: 2026-03-03 | Commit: 8de3b172c34a06240eb5f2de12c11e26ae2b2856*
