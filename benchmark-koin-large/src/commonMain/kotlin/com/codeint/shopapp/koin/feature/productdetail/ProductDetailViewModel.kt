package com.codeint.shopapp.koin.feature.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.domain.product.*
import com.codeint.shopapp.koin.domain.review.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val getReviewListUseCase: GetReviewListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailViewModelUiState>(ProductDetailViewModelUiState.Loading)
    val uiState: StateFlow<ProductDetailViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ProductDetailViewModelUiState.Loading
            try {
                analytics.screen("productdetail")
                _uiState.value = ProductDetailViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProductDetailViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface ProductDetailViewModelUiState {
    data object Loading : ProductDetailViewModelUiState
    data object Success : ProductDetailViewModelUiState
    data class Error(val message: String) : ProductDetailViewModelUiState
}
