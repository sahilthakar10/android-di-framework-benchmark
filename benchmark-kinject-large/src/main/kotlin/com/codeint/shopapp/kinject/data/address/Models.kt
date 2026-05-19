package com.codeint.shopapp.kinject.data.address

data class AddressEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class AddressResponse(val items: List<AddressEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class AddressRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
