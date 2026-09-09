package com.cubelaundry.app.models

data class OrderRequest(
    val customer: Customer,
    val lineItems: List<LineItem>
)

data class Customer(
    val customerName: String,
    val mobileNumber: String,
    val address: String,
    val deliveryType: String = "Delivery",
    val deliveryArea: String = "Qasimabad",
    val paymentStatus: String = "Unpaid",
    val paymentMethod: String = "cash",
    val specialInstructions: String = "",
    val pickupTime: String? = null,
    val deliveryTime: String? = null
)

data class LineItem(
    val itemName: String,
    val serviceType: String,
    val quantity: Int
)
