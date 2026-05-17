package com.codeint.shopapp.metro.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.metro.domain.product.*

class SearchViewModel @Inject constructor(
    private val searchProductUseCase: SearchProductUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchViewModelUiState>(SearchViewModelUiState.Loading)
    val uiState: StateFlow<SearchViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = SearchViewModelUiState.Loading
            try {
                analytics.screen("search")
                _uiState.value = SearchViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = SearchViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface SearchViewModelUiState {
    data object Loading : SearchViewModelUiState
    data object Success : SearchViewModelUiState
    data class Error(val message: String) : SearchViewModelUiState
}
