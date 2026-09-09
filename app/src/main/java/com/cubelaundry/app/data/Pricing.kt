package com.cubelaundry.app.data

/**
 * Client-side pricing rules used to show a live estimate while the cart is
 * being built. The authoritative total is always OrderResponse.grandTotal
 * returned by the backend after placeOrder() succeeds — these functions are
 * for the running total shown on the Order screen only.
 */
object Pricing {

    /** Returns the discount fraction (e.g. 0.10 for 10%). No max cap on the top tier. */
    fun discountPercent(subtotal: Int): Double = when {
        subtotal >= 10000 -> 0.10
        subtotal >= 5000 -> 0.075
        subtotal >= 3000 -> 0.05
        else -> 0.0
    }

    /** Free delivery above Rs. 2,000; otherwise flat fee by area. */
    fun deliveryCharge(subtotal: Int, area: String): Int {
        if (subtotal > 2000) return 0
        return if (area == "Qasimabad") 100 else 200
    }
}
