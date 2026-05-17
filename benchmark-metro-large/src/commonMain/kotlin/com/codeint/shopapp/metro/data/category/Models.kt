package com.codeint.shopapp.metro.data.category

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

data class CategoryEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class CategoryResponse(val items: List<CategoryEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class CategoryRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
