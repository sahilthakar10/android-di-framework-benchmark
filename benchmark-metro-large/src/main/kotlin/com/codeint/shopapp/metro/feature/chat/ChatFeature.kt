package com.codeint.shopapp.metro.feature.chat

import com.codeint.shopapp.metro.domain.chat.*
import com.codeint.shopapp.metro.core.network.WebSocketManager
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.auth.SessionManager
import dev.zacsweers.metro.Inject

class ChatViewModel @Inject constructor(
    private val getChatList: GetChatListUseCase,
    private val createChat: CreateChatUseCase,
    private val webSocketManager: WebSocketManager,
    private val sessionManager: SessionManager,
    private val analytics: AnalyticsTracker
) {
    fun loadConversations(): List<ChatDomainModel> { analytics.screen("chat"); return getChatList.execute().items }
    fun sendMessage(conversationId: String, text: String) { webSocketManager.send(text); analytics.track("chat_send") }
    fun startNewConversation(subject: String): ChatDomainModel = createChat.execute(ChatDomainModel("", subject))
}

class ChatNotificationHandler @Inject constructor(
    private val notificationManager: com.codeint.shopapp.metro.core.notification.NotificationManager,
    private val analytics: AnalyticsTracker
) {
    fun handleNewMessage(from: String, preview: String) { notificationManager.scheduleLocal("New message from $from", preview, 0) }
}

class TypingIndicatorManager @Inject constructor(private val webSocketManager: WebSocketManager) {
    fun startTyping(conversationId: String) { webSocketManager.send("typing_start") }
    fun stopTyping(conversationId: String) { webSocketManager.send("typing_stop") }
}
