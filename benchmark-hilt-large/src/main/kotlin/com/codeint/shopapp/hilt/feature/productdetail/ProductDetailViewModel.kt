package com.codeint.shopapp.hilt.feature.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.product.*
import com.codeint.shopapp.hilt.domain.review.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetail: GetProductDetailUseCase,
    private val getReviewList: GetReviewListUseCase,
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
                val product = getProductDetail.execute("1")
                val reviews = getReviewList.execute()
                _uiState.value = ProductDetailViewModelUiState.Success(product, reviews.items)
            } catch (e: Exception) {
                _uiState.value = ProductDetailViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface ProductDetailViewModelUiState {
    data object Loading : ProductDetailViewModelUiState
    data class Success(val product: ProductDomainModel?, val reviews: List<ReviewDomainModel>) : ProductDetailViewModelUiState
    data class Error(val message: String) : ProductDetailViewModelUiState
}
