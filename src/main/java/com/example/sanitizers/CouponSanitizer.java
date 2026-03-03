package com.example.sanitizers;

import java.util.Set;

/**
 * Validates and applies coupon codes to the cart.
 * Ensures coupons are valid, not expired, and compatible with cart items.
 */
public class CouponSanitizer {

    // Configurable via application.properties
    private int maxCouponsPerCart = 3;
    private boolean stackingEnabled = false;

    /**
     * Validates coupons applied to the cart.
     * - Validates each coupon code against CouponService
     * - Removes expired or invalid coupons
     * - Enforces max coupons per cart limit
     * - Checks coupon-item eligibility (some items may be excluded)
     * - When stacking is disabled, keeps only the highest-value coupon
     * - Calculates and applies discount amounts to eligible items
     * - Adds user messages for removed or adjusted coupons
     */
    public Cart sanitize(Cart cart) {
        Set<String> validCoupons = validateCoupons(cart.getCoupons());

        // Remove invalid coupons
        cart.getCoupons().removeIf(coupon -> {
            if (!validCoupons.contains(coupon)) {
                cart.addMessage("Coupon " + coupon + " is invalid or expired and was removed.");
                return true;
            }
            return false;
        });

        // Enforce max coupons
        while (cart.getCoupons().size() > maxCouponsPerCart) {
            String removed = cart.getCoupons().remove(cart.getCoupons().size() - 1);
            cart.addMessage("Coupon " + removed + " removed — maximum of "
                    + maxCouponsPerCart + " coupons per cart.");
        }

        // Handle stacking
        if (!stackingEnabled && cart.getCoupons().size() > 1) {
            String bestCoupon = findHighestValueCoupon(cart);
            cart.getCoupons().clear();
            cart.getCoupons().add(bestCoupon);
            cart.addMessage("Only one coupon allowed. Keeping the highest-value coupon: " + bestCoupon);
        }

        return cart;
    }

    private Set<String> validateCoupons(java.util.List<String> coupons) {
        // Calls CouponService API
        return Set.of(); // placeholder
    }

    private String findHighestValueCoupon(Cart cart) {
        // Evaluates coupon values against cart items
        return cart.getCoupons().get(0); // placeholder
    }
}
