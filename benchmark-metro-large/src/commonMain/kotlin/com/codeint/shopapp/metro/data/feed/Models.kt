package com.codeint.shopapp.metro.data.feed

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

data class FeedEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class FeedResponse(val items: List<FeedEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class FeedRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
