package com.codeint.shopapp.hilt.feature.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.review.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val getReviewList: GetReviewListUseCase,
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
                val reviews = getReviewList.execute()
                _uiState.value = ReviewsViewModelUiState.Success(reviews.items)
            } catch (e: Exception) {
                _uiState.value = ReviewsViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface ReviewsViewModelUiState {
    data object Loading : ReviewsViewModelUiState
    data class Success(val reviews: List<ReviewDomainModel>) : ReviewsViewModelUiState
    data class Error(val message: String) : ReviewsViewModelUiState
}
