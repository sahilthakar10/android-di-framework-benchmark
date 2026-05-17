package com.codeint.shopapp.metro.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.codeint.shopapp.metro.core.notification.DeepLinkHandler

class NotificationsViewModel @Inject constructor(
    private val deepLinkHandler: DeepLinkHandler,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationsViewModelUiState>(NotificationsViewModelUiState.Loading)
    val uiState: StateFlow<NotificationsViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = NotificationsViewModelUiState.Loading
            try {
                analytics.screen("notifications")
                _uiState.value = NotificationsViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = NotificationsViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface NotificationsViewModelUiState {
    data object Loading : NotificationsViewModelUiState
    data object Success : NotificationsViewModelUiState
    data class Error(val message: String) : NotificationsViewModelUiState
}
