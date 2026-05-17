package com.codeint.shopapp.metro.data.wishlist.mapper

import com.codeint.shopapp.metro.data.wishlist.*
import com.codeint.shopapp.metro.domain.wishlist.*
import dev.zacsweers.metro.Inject

class WishlistMapper @Inject constructor() {
    fun toDomain(e: WishlistEntity) = WishlistDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<WishlistEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: WishlistDomainModel) = WishlistEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: WishlistResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
