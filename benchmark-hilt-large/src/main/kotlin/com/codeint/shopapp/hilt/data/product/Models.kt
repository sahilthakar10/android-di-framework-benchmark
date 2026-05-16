package com.codeint.shopapp.hilt.data.product

data class ProductEntity(
    val id: String,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
    val isActive: Boolean = true
)

data class ProductResponse(
    val items: List<ProductEntity>,
    val totalCount: Int,
    val page: Int,
    val hasMore: Boolean
)

data class ProductRequest(
    val query: String = "",
    val page: Int = 0,
    val pageSize: Int = 20,
    val sortBy: String = "createdAt",
    val filters: Map<String, String> = emptyMap()
)
