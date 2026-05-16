package com.codeint.shopapp.metro.domain.chat

data class ChatDomainModel(
    val id: String,
    val name: String,
    val description: String = "",
    val isActive: Boolean = true,
    val metadata: Map<String, String> = emptyMap()
)

data class PagedResult<T>(
    val items: List<T>,
    val totalCount: Int,
    val page: Int,
    val hasMore: Boolean
)
