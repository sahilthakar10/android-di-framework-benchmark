package com.codeint.shopapp.metro.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.metro.domain.cart.*

class CartViewModel @Inject constructor(
    private val getCartListUseCase: GetCartListUseCase,
    private val deleteCartUseCase: DeleteCartUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartViewModelUiState>(CartViewModelUiState.Loading)
    val uiState: StateFlow<CartViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = CartViewModelUiState.Loading
            try {
                analytics.screen("cart")
                _uiState.value = CartViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = CartViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface CartViewModelUiState {
    data object Loading : CartViewModelUiState
    data object Success : CartViewModelUiState
    data class Error(val message: String) : CartViewModelUiState
}
