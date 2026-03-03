package com.example.services;

import com.example.sanitizers.DeduplicationSanitizer;
import com.example.sanitizers.PriceSanitizer;
import com.example.sanitizers.InventorySanitizer;
import com.example.sanitizers.QuantityLimitSanitizer;
import com.example.sanitizers.CouponSanitizer;

/**
 * Orchestrates the cart sanitization pipeline.
 * Each sanitizer runs in sequence; the output of one feeds into the next.
 */
public class CartSanitizerService {

    private final DeduplicationSanitizer deduplicationSanitizer;
    private final PriceSanitizer priceSanitizer;
    private final InventorySanitizer inventorySanitizer;
    private final QuantityLimitSanitizer quantityLimitSanitizer;
    private final CouponSanitizer couponSanitizer;

    public CartSanitizerService(
            DeduplicationSanitizer deduplicationSanitizer,
            PriceSanitizer priceSanitizer,
            InventorySanitizer inventorySanitizer,
            QuantityLimitSanitizer quantityLimitSanitizer,
            CouponSanitizer couponSanitizer) {
        this.deduplicationSanitizer = deduplicationSanitizer;
        this.priceSanitizer = priceSanitizer;
        this.inventorySanitizer = inventorySanitizer;
        this.quantityLimitSanitizer = quantityLimitSanitizer;
        this.couponSanitizer = couponSanitizer;
    }

    /**
     * Runs the full sanitization pipeline on the given cart.
     * Pipeline execution order:
     * 1. DeduplicationSanitizer   - remove duplicate SKUs
     * 2. PriceSanitizer           - validate and correct pricing
     * 3. InventorySanitizer       - check stock availability
     * 4. QuantityLimitSanitizer   - enforce per-item and cart-wide quantity limits
     * 5. CouponSanitizer          - validate and apply coupon codes
     */
    public Cart sanitize(Cart cart) {
        cart = deduplicationSanitizer.sanitize(cart);   // Step 1
        cart = priceSanitizer.sanitize(cart);           // Step 2
        cart = inventorySanitizer.sanitize(cart);       // Step 3
        cart = quantityLimitSanitizer.sanitize(cart);   // Step 4
        cart = couponSanitizer.sanitize(cart);          // Step 5
        return cart;
    }
}
