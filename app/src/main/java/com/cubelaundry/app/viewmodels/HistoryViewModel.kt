package com.cubelaundry.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cubelaundry.app.api.RetrofitClient
import com.cubelaundry.app.data.Prefs
import com.cubelaundry.app.models.HistoryOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = false,
    val orders: List<HistoryOrder>? = null,
    val noMobileOnFile: Boolean = false,
    val error: String? = null
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state

    fun loadHistory() {
        val mobile = Prefs.getMobileNumber(getApplication())
        if (mobile.isNullOrBlank()) {
            _state.value = _state.value.copy(noMobileOnFile = true, isLoading = false)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, noMobileOnFile = false)
            try {
                val response = RetrofitClient.instance.getHistory(mobile = mobile)
                if (response.ok) {
                    _state.value = _state.value.copy(isLoading = false, orders = response.orders ?: emptyList())
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = response.message ?: "Could not load history"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Network error: ${e.message}")
            }
        }
    }
}
