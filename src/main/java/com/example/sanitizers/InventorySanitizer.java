package com.example.sanitizers;

/**
 * Checks stock availability for cart items.
 * Adjusts quantities or removes items that are out of stock.
 * Supports backorder for eligible items when stock is unavailable.
 */
public class InventorySanitizer {

    // Configurable via application.properties
    private boolean autoAdjustQuantity = true;
    private boolean backorderEnabled = false;
    private int maxBackorderQuantity = 5;

    /**
     * Validates inventory for all items in the cart.
     * - Calls InventoryService to check available stock per SKU
     * - If quantity exceeds stock: adjusts down (when autoAdjustQuantity=true) or removes item
     * - Removes items with zero stock (unless backorder-eligible)
     * - When backorderEnabled=true, allows out-of-stock items up to maxBackorderQuantity
     * - Sets 'inStock' and 'backordered' flags on each item
     * - Adds user message when quantity was adjusted or item is backordered
     */
    public Cart sanitize(Cart cart) {
        cart.getItems().removeIf(item -> {
            int available = checkStock(item.getSku());

            if (available <= 0) {
                if (backorderEnabled && isBackorderEligible(item.getSku())) {
                    int backorderQty = Math.min(item.getQuantity(), maxBackorderQuantity);
                    item.setQuantity(backorderQty);
                    item.setInStock(false);
                    item.setBackordered(true);
                    cart.addMessage(item.getSku() + " is out of stock. "
                            + backorderQty + " unit(s) placed on backorder.");
                    return false; // keep as backorder
                }
                cart.addMessage(item.getSku() + " is out of stock and was removed.");
                return true; // remove
            }

            item.setInStock(true);
            item.setBackordered(false);

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

    private boolean isBackorderEligible(String sku) {
        // Calls InventoryService to check backorder eligibility
        return false; // placeholder
    }
}
