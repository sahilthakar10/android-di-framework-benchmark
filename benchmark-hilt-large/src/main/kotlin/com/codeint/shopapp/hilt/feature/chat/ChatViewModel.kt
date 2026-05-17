package com.codeint.shopapp.hilt.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.chat.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatList: GetChatListUseCase,
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
                val chats = getChatList.execute()
                _uiState.value = ChatViewModelUiState.Success(chats.items)
            } catch (e: Exception) {
                _uiState.value = ChatViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface ChatViewModelUiState {
    data object Loading : ChatViewModelUiState
    data class Success(val conversations: List<ChatDomainModel>) : ChatViewModelUiState
    data class Error(val message: String) : ChatViewModelUiState
}
