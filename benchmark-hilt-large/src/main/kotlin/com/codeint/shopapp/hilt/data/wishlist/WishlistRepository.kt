package com.codeint.shopapp.hilt.data.wishlist

import com.codeint.shopapp.hilt.domain.wishlist.*

interface WishlistRepository {
    fun getAll(request: WishlistRequest = WishlistRequest()): PagedResult<WishlistDomainModel>
    fun getById(id: String): WishlistDomainModel?
    fun create(model: WishlistDomainModel): WishlistDomainModel
    fun update(id: String, model: WishlistDomainModel): WishlistDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<WishlistDomainModel>
    fun clearCache()
}
