package com.cubelaundry.app.models

data class RatesResponse(
    val ok: Boolean,
    val rates: Map<String, Map<String, Int?>>?,
    val message: String?
)

data class OrderResponse(
    val ok: Boolean,
    val invoiceNumber: String?,
    val grandTotal: Int?,
    val invoiceLink: String?,
    val message: String?
)

data class InvoiceResponse(
    val ok: Boolean,
    val order: Order?,
    val items: List<OrderItem>?,
    val message: String?
)

data class Order(
    val id: Int,
    val invoice_number: String,
    val customer_name: String,
    val mobile_number: String,
    val address: String,
    val delivery_type: String,
    val delivery_area: String?,
    val payment_status: String,
    val payment_method: String,
    val items_subtotal: Int,
    val discount_amount: Int,
    val small_order_fee: Int,
    val delivery_charges: Int,
    val grand_total: Int,
    val created_at: String,
    val status: String
)

data class OrderItem(
    val item_name: String,
    val service_type: String,
    val quantity: Int,
    val rate: Int,
    val subtotal: Int
)

data class HistoryResponse(
    val ok: Boolean,
    val orders: List<HistoryOrder>?,
    val message: String?
)

data class HistoryOrder(
    val invoice_number: String,
    val customer_name: String,
    val grand_total: Int,
    val created_at: String,
    val status: String
)
