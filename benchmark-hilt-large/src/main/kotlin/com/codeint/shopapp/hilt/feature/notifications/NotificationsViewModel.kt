package com.codeint.shopapp.hilt.feature.notifications

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
class NotificationsViewModel @Inject constructor(
    private val deepLinkHandler: com.codeint.shopapp.hilt.core.notification.DeepLinkHandler,
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
                _uiState.value = NotificationsViewModelUiState.Success(emptyList())
            } catch (e: Exception) {
                _uiState.value = NotificationsViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface NotificationsViewModelUiState {
    data object Loading : NotificationsViewModelUiState
    data class Success(val notifications: List<String>) : NotificationsViewModelUiState
    data class Error(val message: String) : NotificationsViewModelUiState
}
