package com.example.sanitizers;

/**
 * Validates shipping eligibility and calculates shipping costs for cart items.
 * Removes items that cannot be shipped to the customer's address.
 */
public class ShippingSanitizer {

    // Configurable via application.properties
    private boolean freeShippingEnabled = true;
    private double freeShippingThreshold = 49.99;

    /**
     * Validates shipping for all items in the cart.
     * - Calls ShippingService to check item eligibility for the destination
     * - Removes items that cannot be shipped (hazmat, oversized, restricted)
     * - Calculates shipping cost per item based on weight and destination
     * - Applies free shipping when cart total exceeds freeShippingThreshold
     * - Sets 'shippable' flag on each item
     * - Adds user messages for removed or adjusted items
     */
    public Cart sanitize(Cart cart) {
        cart.getItems().removeIf(item -> {
            if (!isShippable(item.getSku(), cart.getDestination())) {
                cart.addMessage(item.getSku() + " cannot be shipped to your address and was removed.");
                return true;
            }

            item.setShippable(true);
            double cost = calculateShippingCost(item);
            item.setShippingCost(cost);
            return false;
        });

        // Apply free shipping
        if (freeShippingEnabled && cart.getSubtotal() >= freeShippingThreshold) {
            cart.getItems().forEach(item -> item.setShippingCost(0.0));
            cart.addMessage("Free shipping applied — order exceeds $" + freeShippingThreshold + ".");
        }

        return cart;
    }

    private boolean isShippable(String sku, String destination) {
        // Calls ShippingService API
        return true; // placeholder
    }

    private double calculateShippingCost(CartItem item) {
        // Calls ShippingService API
        return 0.0; // placeholder
    }
}
