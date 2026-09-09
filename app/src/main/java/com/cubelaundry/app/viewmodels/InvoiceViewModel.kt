package com.cubelaundry.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cubelaundry.app.api.RetrofitClient
import com.cubelaundry.app.models.InvoiceResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class InvoiceUiState(
    val isLoading: Boolean = false,
    val invoice: InvoiceResponse? = null,
    val error: String? = null
)

class InvoiceViewModel : ViewModel() {
    private val _state = MutableStateFlow(InvoiceUiState())
    val state: StateFlow<InvoiceUiState> = _state

    fun loadInvoice(invoiceNumber: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.instance.getInvoice(invoice = invoiceNumber)
                if (response.ok) {
                    _state.value = _state.value.copy(isLoading = false, invoice = response)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = response.message ?: "Invoice not found"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Network error: ${e.message}")
            }
        }
    }
}
