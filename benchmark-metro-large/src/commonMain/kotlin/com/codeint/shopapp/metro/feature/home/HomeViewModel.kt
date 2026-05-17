package com.codeint.shopapp.metro.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.metro.domain.product.*
import com.codeint.shopapp.metro.domain.category.*
import com.codeint.shopapp.metro.domain.promotion.*
import com.codeint.shopapp.metro.domain.feed.*
import com.codeint.shopapp.metro.core.config.FeatureFlagManager

class HomeViewModel @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase,
    private val getCategoryListUseCase: GetCategoryListUseCase,
    private val getPromotionListUseCase: GetPromotionListUseCase,
    private val getFeedListUseCase: GetFeedListUseCase,
    private val featureFlagManager: FeatureFlagManager,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeViewModelUiState>(HomeViewModelUiState.Loading)
    val uiState: StateFlow<HomeViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeViewModelUiState.Loading
            try {
                analytics.screen("home")
                _uiState.value = HomeViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = HomeViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface HomeViewModelUiState {
    data object Loading : HomeViewModelUiState
    data object Success : HomeViewModelUiState
    data class Error(val message: String) : HomeViewModelUiState
}
