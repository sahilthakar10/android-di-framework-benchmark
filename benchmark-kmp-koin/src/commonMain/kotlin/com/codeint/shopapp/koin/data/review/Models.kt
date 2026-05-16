package com.codeint.shopapp.koin.data.review

data class ReviewEntity(
    val id: String,
    val name: String,
    val description: String = "",
    val createdAt: Long = com.codeint.shopapp.common.platform.currentTimeMillis(),
    val updatedAt: Long = com.codeint.shopapp.common.platform.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
    val isActive: Boolean = true
)

data class ReviewResponse(
    val items: List<ReviewEntity>,
    val totalCount: Int,
    val page: Int,
    val hasMore: Boolean
)

data class ReviewRequest(
    val query: String = "",
    val page: Int = 0,
    val pageSize: Int = 20,
    val sortBy: String = "createdAt",
    val filters: Map<String, String> = emptyMap()
)
