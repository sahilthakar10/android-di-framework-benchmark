package com.codeint.shopapp.metro.data.cart

data class CartEntity(
    val id: String,
    val name: String,
    val description: String = "",
    val createdAt: Long = com.codeint.shopapp.common.platform.currentTimeMillis(),
    val updatedAt: Long = com.codeint.shopapp.common.platform.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
    val isActive: Boolean = true
)

data class CartResponse(
    val items: List<CartEntity>,
    val totalCount: Int,
    val page: Int,
    val hasMore: Boolean
)

data class CartRequest(
    val query: String = "",
    val page: Int = 0,
    val pageSize: Int = 20,
    val sortBy: String = "createdAt",
    val filters: Map<String, String> = emptyMap()
)
