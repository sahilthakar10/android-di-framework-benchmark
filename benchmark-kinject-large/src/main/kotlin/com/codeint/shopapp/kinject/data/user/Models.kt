package com.codeint.shopapp.kinject.data.user

data class UserEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class UserResponse(val items: List<UserEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class UserRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
