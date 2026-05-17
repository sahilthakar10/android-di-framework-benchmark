package com.codeint.shopapp.metro.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.metro.core.auth.AuthManager

class OnboardingViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingViewModelUiState>(OnboardingViewModelUiState.Loading)
    val uiState: StateFlow<OnboardingViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = OnboardingViewModelUiState.Loading
            try {
                analytics.screen("onboarding")
                _uiState.value = OnboardingViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = OnboardingViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface OnboardingViewModelUiState {
    data object Loading : OnboardingViewModelUiState
    data object Success : OnboardingViewModelUiState
    data class Error(val message: String) : OnboardingViewModelUiState
}
