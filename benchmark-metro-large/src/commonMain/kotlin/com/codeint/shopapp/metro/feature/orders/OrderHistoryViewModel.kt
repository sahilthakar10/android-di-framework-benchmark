package com.codeint.shopapp.metro.feature.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.metro.domain.order.*

class OrderHistoryViewModel @Inject constructor(
    private val getOrderListUseCase: GetOrderListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderHistoryViewModelUiState>(OrderHistoryViewModelUiState.Loading)
    val uiState: StateFlow<OrderHistoryViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = OrderHistoryViewModelUiState.Loading
            try {
                analytics.screen("orders")
                _uiState.value = OrderHistoryViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = OrderHistoryViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface OrderHistoryViewModelUiState {
    data object Loading : OrderHistoryViewModelUiState
    data object Success : OrderHistoryViewModelUiState
    data class Error(val message: String) : OrderHistoryViewModelUiState
}
