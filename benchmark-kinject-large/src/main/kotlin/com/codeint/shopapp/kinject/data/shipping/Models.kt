package com.codeint.shopapp.kinject.data.shipping

data class ShippingEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class ShippingResponse(val items: List<ShippingEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class ShippingRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
