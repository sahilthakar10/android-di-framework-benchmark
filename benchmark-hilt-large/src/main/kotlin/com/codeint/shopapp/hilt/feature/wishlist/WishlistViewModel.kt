package com.codeint.shopapp.hilt.feature.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.wishlist.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistList: GetWishlistListUseCase,
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
                val items = getWishlistList.execute()
                _uiState.value = WishlistViewModelUiState.Success(items.items)
            } catch (e: Exception) {
                _uiState.value = WishlistViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface WishlistViewModelUiState {
    data object Loading : WishlistViewModelUiState
    data class Success(val items: List<WishlistDomainModel>) : WishlistViewModelUiState
    data class Error(val message: String) : WishlistViewModelUiState
}
