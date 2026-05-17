package com.codeint.shopapp.koin.data.cart

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

data class CartEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class CartResponse(val items: List<CartEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class CartRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
