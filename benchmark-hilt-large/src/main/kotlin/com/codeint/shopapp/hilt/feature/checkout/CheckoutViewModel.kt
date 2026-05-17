package com.codeint.shopapp.hilt.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.order.*
import com.codeint.shopapp.hilt.domain.address.*
import com.codeint.shopapp.hilt.domain.payment.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val createOrder: CreateOrderUseCase,
    private val getAddressList: GetAddressListUseCase,
    private val getPaymentList: GetPaymentListUseCase,
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
                val addresses = getAddressList.execute()
                val payments = getPaymentList.execute()
                _uiState.value = CheckoutViewModelUiState.Success(addresses.items, payments.items)
            } catch (e: Exception) {
                _uiState.value = CheckoutViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface CheckoutViewModelUiState {
    data object Loading : CheckoutViewModelUiState
    data class Success(val addresses: List<AddressDomainModel>, val payments: List<PaymentDomainModel>) : CheckoutViewModelUiState
    data class Error(val message: String) : CheckoutViewModelUiState
}
