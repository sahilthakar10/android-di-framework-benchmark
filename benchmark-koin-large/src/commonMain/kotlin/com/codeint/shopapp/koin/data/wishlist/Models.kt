package com.codeint.shopapp.koin.data.wishlist

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

data class WishlistEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class WishlistResponse(val items: List<WishlistEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class WishlistRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
