package com.codeint.shopapp.kinject.data.wishlist

data class WishlistEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class WishlistResponse(val items: List<WishlistEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class WishlistRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
