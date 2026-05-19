package com.codeint.shopapp.kinject.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.kinject.domain.product.*
import com.codeint.shopapp.kinject.domain.category.*
import com.codeint.shopapp.kinject.domain.promotion.*
import com.codeint.shopapp.kinject.domain.feed.*
import com.codeint.shopapp.kinject.core.config.FeatureFlagManager

@Inject class HomeViewModel(
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
