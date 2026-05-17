package com.codeint.shopapp.hilt.feature.settings

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
class SettingsViewModel @Inject constructor(
    private val themeManager: com.codeint.shopapp.hilt.core.config.ThemeManager,
    private val localeManager: com.codeint.shopapp.hilt.core.config.LocaleManager,
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
                _uiState.value = SettingsViewModelUiState.Success(
                    isDarkMode = themeManager.isDarkMode(),
                    locale = localeManager.getCurrentLocale()
                )
            } catch (e: Exception) {
                _uiState.value = SettingsViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface SettingsViewModelUiState {
    data object Loading : SettingsViewModelUiState
    data class Success(val isDarkMode: Boolean, val locale: String) : SettingsViewModelUiState
    data class Error(val message: String) : SettingsViewModelUiState
}
