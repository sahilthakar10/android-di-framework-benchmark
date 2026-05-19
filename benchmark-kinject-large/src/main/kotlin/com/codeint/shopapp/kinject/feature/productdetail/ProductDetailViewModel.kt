package com.codeint.shopapp.kinject.feature.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.kinject.domain.product.*
import com.codeint.shopapp.kinject.domain.review.*

@Inject class ProductDetailViewModel(
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
