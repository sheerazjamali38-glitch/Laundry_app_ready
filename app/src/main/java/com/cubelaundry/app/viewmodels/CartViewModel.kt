package com.cubelaundry.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cubelaundry.app.api.RetrofitClient
import com.cubelaundry.app.data.Pricing
import com.cubelaundry.app.data.Prefs
import com.cubelaundry.app.models.Customer
import com.cubelaundry.app.models.LineItem
import com.cubelaundry.app.models.OrderRequest
import com.cubelaundry.app.models.OrderResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Identifies one cart line: a specific item at a specific service type. */
data class CartKey(val itemName: String, val serviceType: String)

/** A single row shown on the Cart screen. */
data class CartLine(
    val itemName: String,
    val serviceType: String,
    val quantity: Int,
    val unitPrice: Int,
    val subtotal: Int
)

/**
 * Shared cart + checkout state used by both the Item Selection screen (adds
 * items) and the Cart screen (reviews, edits, and submits the order). Held
 * once at the Activity level so it survives navigation between bottom-nav
 * tabs.
 */
data class CartUiState(
    val isLoadingRates: Boolean = false,
    val ratesError: String? = null,
    val rates: Map<String, Map<String, Int?>> = emptyMap(),
    val quantities: Map<CartKey, Int> = emptyMap(),
    val deliveryArea: String = "Qasimabad",
    val paymentMethod: String = "cash",
    val isSubmitting: Boolean = false,
    val orderResult: OrderResponse? = null,
    val error: String? = null
) {
    val lines: List<CartLine>
        get() = quantities.entries
            .filter { it.value > 0 }
            .mapNotNull { (key, qty) ->
                val price = rates[key.itemName]?.get(key.serviceType) ?: return@mapNotNull null
                CartLine(key.itemName, key.serviceType, qty, price, price * qty)
            }
            .sortedBy { it.itemName }

    val subtotal: Int
        get() = lines.sumOf { it.subtotal }

    val itemCount: Int
        get() = quantities.values.sum()

    val discountPercent: Double get() = Pricing.discountPercent(subtotal)
    val discountAmount: Int get() = (subtotal * discountPercent).roundToInt()
    val deliveryCharge: Int get() = Pricing.deliveryCharge(subtotal, deliveryArea)
    val grandTotalEstimate: Int get() = subtotal - discountAmount + deliveryCharge
}

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(CartUiState())
    val state: StateFlow<CartUiState> = _state

    init {
        loadRates()
    }

    fun loadRates() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingRates = true, ratesError = null)
            try {
                val response = RetrofitClient.instance.getRates()
                if (response.ok && response.rates != null) {
                    _state.value = _state.value.copy(isLoadingRates = false, rates = response.rates)
                } else {
                    _state.value = _state.value.copy(
                        isLoadingRates = false,
                        ratesError = response.message ?: "Could not load rates"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingRates = false,
                    ratesError = "Network error: ${e.message}"
                )
            }
        }
    }

    fun setQuantity(itemName: String, serviceType: String, quantity: Int) {
        val key = CartKey(itemName, serviceType)
        val updated = _state.value.quantities.toMutableMap()
        if (quantity <= 0) updated.remove(key) else updated[key] = quantity
        _state.value = _state.value.copy(quantities = updated)
    }

    fun quantityFor(itemName: String, serviceType: String): Int =
        _state.value.quantities[CartKey(itemName, serviceType)] ?: 0

    fun removeLine(itemName: String, serviceType: String) {
        setQuantity(itemName, serviceType, 0)
    }

    fun setDeliveryArea(area: String) {
        _state.value = _state.value.copy(deliveryArea = area)
    }

    fun setPaymentMethod(method: String) {
        _state.value = _state.value.copy(paymentMethod = method)
    }

    fun clearOrderResult() {
        _state.value = _state.value.copy(orderResult = null, error = null)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun placeOrder(name: String, phone: String, address: String, specialInstructions: String) {
        val current = _state.value
        val lineItems = current.quantities.entries
            .filter { it.value > 0 }
            .map { (key, qty) -> LineItem(itemName = key.itemName, serviceType = key.serviceType, quantity = qty) }

        if (lineItems.isEmpty() || name.isBlank() || phone.isBlank() || address.isBlank()) {
            _state.value = current.copy(error = "Please fill in your details and add at least one item.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            try {
                val customer = Customer(
                    customerName = name,
                    mobileNumber = phone,
                    address = address,
                    deliveryArea = current.deliveryArea,
                    paymentMethod = current.paymentMethod,
                    specialInstructions = specialInstructions
                )
                val request = OrderRequest(customer, lineItems)
                val response = RetrofitClient.instance.placeOrder(order = request)
                if (response.ok) {
                    Prefs.saveProfile(getApplication(), name, phone, address)
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        orderResult = response,
                        quantities = emptyMap()
                    )
                } else {
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        error = response.message ?: "Order failed"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    error = "Network error: ${e.message}"
                )
            }
        }
    }
}
