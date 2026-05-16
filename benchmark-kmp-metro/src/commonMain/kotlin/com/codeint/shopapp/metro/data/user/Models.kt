package com.codeint.shopapp.metro.data.user

data class UserEntity(
    val id: String,
    val name: String,
    val description: String = "",
    val createdAt: Long = com.codeint.shopapp.common.platform.currentTimeMillis(),
    val updatedAt: Long = com.codeint.shopapp.common.platform.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
    val isActive: Boolean = true
)

data class UserResponse(
    val items: List<UserEntity>,
    val totalCount: Int,
    val page: Int,
    val hasMore: Boolean
)

data class UserRequest(
    val query: String = "",
    val page: Int = 0,
    val pageSize: Int = 20,
    val sortBy: String = "createdAt",
    val filters: Map<String, String> = emptyMap()
)
