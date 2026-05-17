package com.codeint.shopapp.hilt.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.product.*
import com.codeint.shopapp.hilt.domain.category.*
import com.codeint.shopapp.hilt.domain.promotion.*
import com.codeint.shopapp.hilt.domain.feed.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.config.FeatureFlagManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductList: GetProductListUseCase,
    private val getCategoryList: GetCategoryListUseCase,
    private val getPromotionList: GetPromotionListUseCase,
    private val getFeedList: GetFeedListUseCase,
    private val analytics: AnalyticsTracker,
    private val featureFlags: FeatureFlagManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                analytics.screen("home")
                val products = getProductList.execute(pageSize = 10)
                val categories = getCategoryList.execute()
                val promotions = getPromotionList.execute()
                _uiState.value = HomeUiState.Success(
                    products = products.items,
                    categories = categories.items,
                    promotions = promotions.items,
                    showNewBanner = featureFlags.isEnabled("new_home_banner")
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val products: List<ProductDomainModel>,
        val categories: List<CategoryDomainModel>,
        val promotions: List<PromotionDomainModel>,
        val showNewBanner: Boolean
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
