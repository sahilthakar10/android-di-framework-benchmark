package com.codeint.shopapp.kinject.data.promotion

data class PromotionEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class PromotionResponse(val items: List<PromotionEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class PromotionRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
