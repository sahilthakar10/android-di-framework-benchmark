package com.codeint.shopapp.metro.feature.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.metro.domain.review.*

class ReviewsViewModel @Inject constructor(
    private val getReviewListUseCase: GetReviewListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewsViewModelUiState>(ReviewsViewModelUiState.Loading)
    val uiState: StateFlow<ReviewsViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ReviewsViewModelUiState.Loading
            try {
                analytics.screen("reviews")
                _uiState.value = ReviewsViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = ReviewsViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface ReviewsViewModelUiState {
    data object Loading : ReviewsViewModelUiState
    data object Success : ReviewsViewModelUiState
    data class Error(val message: String) : ReviewsViewModelUiState
}
