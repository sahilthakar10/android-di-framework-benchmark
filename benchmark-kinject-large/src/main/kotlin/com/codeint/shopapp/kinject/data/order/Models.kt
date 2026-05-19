package com.codeint.shopapp.kinject.data.order

data class OrderEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class OrderResponse(val items: List<OrderEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class OrderRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
