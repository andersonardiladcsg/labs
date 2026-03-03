package com.example.services;

import com.example.sanitizers.DuplicateSanitizer;
import com.example.sanitizers.PriceSanitizer;
import com.example.sanitizers.InventorySanitizer;

/**
 * Orchestrates the cart sanitization pipeline.
 * Each sanitizer runs in sequence; the output of one feeds into the next.
 */
public class CartSanitizerService {

    private final DuplicateSanitizer duplicateSanitizer;
    private final PriceSanitizer priceSanitizer;
    private final InventorySanitizer inventorySanitizer;

    public CartSanitizerService(
            DuplicateSanitizer duplicateSanitizer,
            PriceSanitizer priceSanitizer,
            InventorySanitizer inventorySanitizer) {
        this.duplicateSanitizer = duplicateSanitizer;
        this.priceSanitizer = priceSanitizer;
        this.inventorySanitizer = inventorySanitizer;
    }

    /**
     * Runs the full sanitization pipeline on the given cart.
     * Pipeline execution order:
     * 1. DuplicateSanitizer  - remove duplicate SKUs
     * 2. PriceSanitizer      - validate and correct pricing
     * 3. InventorySanitizer  - check stock availability
     */
    public Cart sanitize(Cart cart) {
        cart = duplicateSanitizer.sanitize(cart);   // Step 1
        cart = priceSanitizer.sanitize(cart);       // Step 2
        cart = inventorySanitizer.sanitize(cart);   // Step 3
        return cart;
    }
}
