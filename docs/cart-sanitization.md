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
| 1 | DeduplicationSanitizer | [`DeduplicationSanitizer.java`](../src/main/java/com/example/sanitizers/DeduplicationSanitizer.java) | Deduplicates cart items by SKU |
| 2 | PriceSanitizer | [`PriceSanitizer.java`](../src/main/java/com/example/sanitizers/PriceSanitizer.java) | Validates and corrects item pricing |
| 3 | InventorySanitizer | [`InventorySanitizer.java`](../src/main/java/com/example/sanitizers/InventorySanitizer.java) | Removes or adjusts items based on stock availability |
| 4 | QuantityLimitSanitizer | [`QuantityLimitSanitizer.java`](../src/main/java/com/example/sanitizers/QuantityLimitSanitizer.java) | Enforces per-item and cart-wide quantity limits |
| 5 | CouponSanitizer | [`CouponSanitizer.java`](../src/main/java/com/example/sanitizers/CouponSanitizer.java) | Validates and applies coupon codes |

---

## 1. DeduplicationSanitizer

**Purpose:** Removes duplicate SKUs from the cart, keeping the first occurrence of each.

### What it does

- Uses a `LinkedHashMap` keyed by SKU to deduplicate while preserving insertion order
- Keeps the first occurrence of each SKU and drops any later duplicates
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
- Removes items that are completely out of stock (unless backorder is enabled for that SKU)
- If quantity exceeds available stock and `autoAdjustQuantity` is enabled, reduces the quantity to match stock
- If quantity exceeds available stock and `autoAdjustQuantity` is disabled, removes the item entirely
- When `backorderEnabled` is `true` and a SKU is backorder-eligible, keeps the item as a backorder with quantity capped at `maxBackorderQuantity`
- Sets `inStock` and `backordered` flags on each remaining item
- Notifies the customer when quantities are adjusted, items are backordered, or items are removed

### Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `sanitizer.inventory.auto-adjust-quantity` | `true` | When `true`, reduces quantity to match stock rather than removing the item |
| `sanitizer.inventory.backorder-enabled` | `false` | When `true`, allows eligible out-of-stock items to be placed on backorder |
| `sanitizer.inventory.max-backorder-quantity` | `5` | Maximum quantity allowed per backordered item |

### Dependencies

| Service | Purpose |
|---------|---------|
| InventoryService | Provides available stock counts and backorder eligibility by SKU |

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

---

## 5. CouponSanitizer

**Purpose:** Validates and applies coupon codes, enforcing stacking rules and per-cart limits.

### What it does

- Validates each coupon code against `CouponService` and removes expired or invalid coupons
- Enforces the maximum number of coupons allowed per cart
- Checks coupon-item eligibility (some items may be excluded from certain coupons)
- When stacking is disabled, keeps only the highest-value coupon and removes the rest
- Calculates and applies discount amounts to eligible items
- Notifies the customer when coupons are removed or adjusted

### Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `sanitizer.coupon.max-per-cart` | `3` | Maximum number of coupon codes allowed per cart |
| `sanitizer.coupon.stacking-enabled` | `false` | When `false`, only the highest-value coupon is kept |

### Dependencies

| Service | Purpose |
|---------|---------|
| CouponService | Validates coupon codes and determines discount values |

<!-- AUTO-END -->

---

*Last updated: 2026-03-03 | Commit: e1f5510b58f1221f3be51d7595d55f545d7e102f*
