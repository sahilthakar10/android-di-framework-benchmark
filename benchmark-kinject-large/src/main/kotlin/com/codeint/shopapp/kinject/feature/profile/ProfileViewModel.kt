package com.codeint.shopapp.kinject.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.kinject.domain.user.*
import com.codeint.shopapp.kinject.domain.order.*

@Inject class ProfileViewModel(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val getOrderListUseCase: GetOrderListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileViewModelUiState>(ProfileViewModelUiState.Loading)
    val uiState: StateFlow<ProfileViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ProfileViewModelUiState.Loading
            try {
                analytics.screen("profile")
                _uiState.value = ProfileViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface ProfileViewModelUiState {
    data object Loading : ProfileViewModelUiState
    data object Success : ProfileViewModelUiState
    data class Error(val message: String) : ProfileViewModelUiState
}
