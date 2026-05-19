package com.codeint.shopapp.kinject.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.kinject.core.auth.AuthManager

@Inject class OnboardingViewModel(
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
