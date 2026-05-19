package com.codeint.shopapp.kinject.data.review

data class ReviewEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class ReviewResponse(val items: List<ReviewEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class ReviewRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
