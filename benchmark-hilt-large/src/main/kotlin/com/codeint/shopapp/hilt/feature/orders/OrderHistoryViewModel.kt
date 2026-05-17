package com.codeint.shopapp.hilt.feature.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.order.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val getOrderList: GetOrderListUseCase,
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
                val orders = getOrderList.execute()
                _uiState.value = OrderHistoryViewModelUiState.Success(orders.items)
            } catch (e: Exception) {
                _uiState.value = OrderHistoryViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface OrderHistoryViewModelUiState {
    data object Loading : OrderHistoryViewModelUiState
    data class Success(val orders: List<OrderDomainModel>) : OrderHistoryViewModelUiState
    data class Error(val message: String) : OrderHistoryViewModelUiState
}
