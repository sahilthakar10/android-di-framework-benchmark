package com.codeint.shopapp.hilt.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.cart.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartList: GetCartListUseCase,
    private val deleteCart: DeleteCartUseCase,
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
                val items = getCartList.execute()
                _uiState.value = CartViewModelUiState.Success(items.items, items.totalCount)
            } catch (e: Exception) {
                _uiState.value = CartViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface CartViewModelUiState {
    data object Loading : CartViewModelUiState
    data class Success(val items: List<CartDomainModel>, val itemCount: Int) : CartViewModelUiState
    data class Error(val message: String) : CartViewModelUiState
}
