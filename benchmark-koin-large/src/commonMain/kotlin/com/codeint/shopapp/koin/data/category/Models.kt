package com.codeint.shopapp.koin.data.category

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

data class CategoryEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class CategoryResponse(val items: List<CategoryEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class CategoryRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
