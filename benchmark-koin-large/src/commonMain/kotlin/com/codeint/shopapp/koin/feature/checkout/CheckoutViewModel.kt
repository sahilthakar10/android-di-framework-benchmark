package com.codeint.shopapp.koin.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.domain.order.*
import com.codeint.shopapp.koin.domain.address.*
import com.codeint.shopapp.koin.domain.payment.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getAddressListUseCase: GetAddressListUseCase,
    private val getPaymentListUseCase: GetPaymentListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutViewModelUiState>(CheckoutViewModelUiState.Loading)
    val uiState: StateFlow<CheckoutViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = CheckoutViewModelUiState.Loading
            try {
                analytics.screen("checkout")
                _uiState.value = CheckoutViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = CheckoutViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface CheckoutViewModelUiState {
    data object Loading : CheckoutViewModelUiState
    data object Success : CheckoutViewModelUiState
    data class Error(val message: String) : CheckoutViewModelUiState
}
