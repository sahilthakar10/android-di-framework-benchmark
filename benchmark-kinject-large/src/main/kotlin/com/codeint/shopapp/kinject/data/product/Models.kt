package com.codeint.shopapp.kinject.data.product

data class ProductEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class ProductResponse(val items: List<ProductEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class ProductRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
