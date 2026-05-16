package com.codeint.shopapp.hilt.data.shipping

data class ShippingEntity(
    val id: String,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
    val isActive: Boolean = true
)

data class ShippingResponse(
    val items: List<ShippingEntity>,
    val totalCount: Int,
    val page: Int,
    val hasMore: Boolean
)

data class ShippingRequest(
    val query: String = "",
    val page: Int = 0,
    val pageSize: Int = 20,
    val sortBy: String = "createdAt",
    val filters: Map<String, String> = emptyMap()
)
