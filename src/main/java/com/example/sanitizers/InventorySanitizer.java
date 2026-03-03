package com.example.sanitizers;

/**
 * Checks stock availability for cart items.
 * Adjusts quantities or removes items that are out of stock.
 */
public class InventorySanitizer {

    // Configurable via application.properties
    private boolean autoAdjustQuantity = true;

    /**
     * Validates inventory for all items in the cart.
     * - Calls InventoryService to check available stock per SKU
     * - If quantity exceeds stock: adjusts down (when autoAdjustQuantity=true) or removes item
     * - Removes items with zero stock
     * - Sets 'inStock' flag on each item
     * - Adds user message when quantity was adjusted
     */
    public Cart sanitize(Cart cart) {
        cart.getItems().removeIf(item -> {
            int available = checkStock(item.getSku());

            if (available <= 0) {
                cart.addMessage(item.getSku() + " is out of stock and was removed.");
                return true; // remove
            }

            item.setInStock(true);

            if (item.getQuantity() > available) {
                if (autoAdjustQuantity) {
                    item.setQuantity(available);
                    cart.addMessage("Quantity adjusted for " + item.getSku()
                            + " to " + available + " (max available).");
                } else {
                    cart.addMessage(item.getSku() + " exceeds available stock.");
                    return true; // remove
                }
            }

            return false; // keep
        });

        return cart;
    }

    private int checkStock(String sku) {
        // Calls InventoryService API
        return 0; // placeholder
    }
}
