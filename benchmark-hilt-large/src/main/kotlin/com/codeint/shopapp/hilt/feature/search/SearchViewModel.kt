package com.codeint.shopapp.hilt.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.product.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProduct: SearchProductUseCase,
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
                val results = searchProduct.execute("", 0)
                _uiState.value = SearchViewModelUiState.Success(results.items, results.totalCount)
            } catch (e: Exception) {
                _uiState.value = SearchViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface SearchViewModelUiState {
    data object Loading : SearchViewModelUiState
    data class Success(val results: List<ProductDomainModel>, val totalCount: Int) : SearchViewModelUiState
    data class Error(val message: String) : SearchViewModelUiState
}
