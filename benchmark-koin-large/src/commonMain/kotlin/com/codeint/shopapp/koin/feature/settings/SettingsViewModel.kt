package com.codeint.shopapp.koin.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.config.ThemeManager
import com.codeint.shopapp.koin.core.config.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val themeManager: ThemeManager,
    private val localeManager: LocaleManager,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsViewModelUiState>(SettingsViewModelUiState.Loading)
    val uiState: StateFlow<SettingsViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = SettingsViewModelUiState.Loading
            try {
                analytics.screen("settings")
                _uiState.value = SettingsViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = SettingsViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface SettingsViewModelUiState {
    data object Loading : SettingsViewModelUiState
    data object Success : SettingsViewModelUiState
    data class Error(val message: String) : SettingsViewModelUiState
}
