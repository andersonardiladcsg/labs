package com.example.sanitizers;

import java.util.Iterator;

/**
 * Enforces quantity limits on cart items and overall cart size.
 * Runs after InventorySanitizer to apply business rules on top of stock availability.
 */
public class QuantityLimitSanitizer {

    // Configurable via application.properties
    private int maxQuantityPerItem = 10;
    private int minQuantityPerItem = 1;
    private int maxCartSize = 50;

    /**
     * Enforces quantity constraints on all cart items.
     * - Clamps each item's quantity to [minQuantityPerItem, maxQuantityPerItem]
     * - If total items exceed maxCartSize, removes items from the end until within limit
     * - Adds user messages for each adjustment
     */
    public Cart sanitize(Cart cart) {
        // Enforce per-item quantity limits
        for (CartItem item : cart.getItems()) {
            if (item.getQuantity() > maxQuantityPerItem) {
                int original = item.getQuantity();
                item.setQuantity(maxQuantityPerItem);
                cart.addMessage("Quantity for " + item.getSku()
                        + " reduced from " + original + " to " + maxQuantityPerItem
                        + " (maximum per item).");
            } else if (item.getQuantity() < minQuantityPerItem) {
                item.setQuantity(minQuantityPerItem);
                cart.addMessage("Quantity for " + item.getSku()
                        + " set to minimum of " + minQuantityPerItem + ".");
            }
        }

        // Enforce max cart size (total unique SKUs)
        if (cart.getItems().size() > maxCartSize) {
            int removed = 0;
            Iterator<CartItem> it = cart.getItems().iterator();
            int index = 0;

            while (it.hasNext()) {
                it.next();
                if (index >= maxCartSize) {
                    it.remove();
                    removed++;
                }
                index++;
            }

            cart.addMessage(removed + " item(s) removed — cart exceeds maximum of "
                    + maxCartSize + " unique items.");
        }

        return cart;
    }
}
