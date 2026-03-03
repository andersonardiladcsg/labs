package com.example.sanitizers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Removes duplicate SKUs from the cart.
 * Keeps the first occurrence of each SKU and discards subsequent duplicates.
 */
public class DuplicateSanitizer {

    /**
     * Removes duplicate items from the cart based on SKU.
     * Uses a LinkedHashMap to preserve insertion order.
     */
    public Cart sanitize(Cart cart) {
        LinkedHashMap<String, CartItem> seen = new LinkedHashMap<>();

        for (CartItem item : cart.getItems()) {
            if (!seen.containsKey(item.getSku())) {
                seen.put(item.getSku(), item);
            } else {
                System.out.println("WARN: Duplicate SKU removed: " + item.getSku());
            }
        }

        cart.setItems(List.copyOf(seen.values()));
        return cart;
    }
}
