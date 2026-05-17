package com.codeint.shopapp.koin.data.search

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

data class SearchEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class SearchResponse(val items: List<SearchEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class SearchRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
