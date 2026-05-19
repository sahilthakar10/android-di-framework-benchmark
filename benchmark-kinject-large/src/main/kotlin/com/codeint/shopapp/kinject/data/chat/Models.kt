package com.codeint.shopapp.kinject.data.chat

data class ChatEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class ChatResponse(val items: List<ChatEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class ChatRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
