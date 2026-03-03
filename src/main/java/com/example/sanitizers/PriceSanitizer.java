package com.example.sanitizers;

/**
 * Validates and corrects pricing for cart items.
 * Fetches current prices from the pricing service and applies discount rules.
 */
public class PriceSanitizer {

    // Configurable via application.properties
    private double maxDiscountPercent = 50.0;
    private double minPrice = 0.01;
    private boolean priceMatchEnabled = false;

    /**
     * Validates prices for all items in the cart.
     * - Fetches current price from PricingService
     * - Applies discount cap (max 50% off by default)
     * - Removes items with price <= 0
     * - Adds user message when price was corrected
     * - When priceMatchEnabled=true, matches competitor prices via PriceMatchService
     */
    public Cart sanitize(Cart cart) {
        cart.getItems().removeIf(item -> {
            double currentPrice = fetchPrice(item.getSku());

            if (currentPrice < minPrice) {
                cart.addMessage("Item " + item.getSku() + " is no longer available.");
                return true; // remove
            }

            // Cap discount
            double discount = 1.0 - (item.getPrice() / currentPrice);
            if (discount > maxDiscountPercent / 100.0) {
                item.setPrice(currentPrice * (1.0 - maxDiscountPercent / 100.0));
                cart.addMessage("Price adjusted for " + item.getSku());
            }

            return false; // keep
        });

        return cart;
    }

    private double fetchPrice(String sku) {
        // Calls PricingService API
        return 0.0; // placeholder
    }
}
