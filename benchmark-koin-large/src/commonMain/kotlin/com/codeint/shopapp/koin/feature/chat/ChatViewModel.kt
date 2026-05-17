package com.codeint.shopapp.koin.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.domain.chat.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getChatListUseCase: GetChatListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatViewModelUiState>(ChatViewModelUiState.Loading)
    val uiState: StateFlow<ChatViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ChatViewModelUiState.Loading
            try {
                analytics.screen("chat")
                _uiState.value = ChatViewModelUiState.Success
            } catch (e: Exception) {
                _uiState.value = ChatViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface ChatViewModelUiState {
    data object Loading : ChatViewModelUiState
    data object Success : ChatViewModelUiState
    data class Error(val message: String) : ChatViewModelUiState
}
