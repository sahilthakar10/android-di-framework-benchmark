package com.codeint.shopapp.metro.data.user

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

data class UserEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class UserResponse(val items: List<UserEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class UserRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
