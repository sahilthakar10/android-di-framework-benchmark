package com.codeint.shopapp.hilt.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authManager: com.codeint.shopapp.hilt.core.auth.AuthManager,
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
                _uiState.value = OnboardingViewModelUiState.Success(authManager.isLoggedIn())
            } catch (e: Exception) {
                _uiState.value = OnboardingViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface OnboardingViewModelUiState {
    data object Loading : OnboardingViewModelUiState
    data class Success(val isLoggedIn: Boolean) : OnboardingViewModelUiState
    data class Error(val message: String) : OnboardingViewModelUiState
}
