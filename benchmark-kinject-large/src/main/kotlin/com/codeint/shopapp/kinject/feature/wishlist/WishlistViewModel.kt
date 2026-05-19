package com.codeint.shopapp.kinject.feature.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.kinject.domain.wishlist.*

@Inject class WishlistViewModel(
    private val getWishlistListUseCase: GetWishlistListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<WishlistViewModelUiState>(WishlistViewModelUiState.Loading)
    val uiState: StateFlow<WishlistViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = WishlistViewModelUiState.Loading
            try {
                analytics.screen("wishlist")
                _uiState.value = WishlistViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = WishlistViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface WishlistViewModelUiState {
    data object Loading : WishlistViewModelUiState
    data object Success : WishlistViewModelUiState
    data class Error(val message: String) : WishlistViewModelUiState
}
