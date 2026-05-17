package com.codeint.shopapp.koin.data.wishlist.mapper

import com.codeint.shopapp.koin.data.wishlist.*
import com.codeint.shopapp.koin.domain.wishlist.*

class WishlistMapper {
    fun toDomain(e: WishlistEntity) = WishlistDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<WishlistEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: WishlistDomainModel) = WishlistEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: WishlistResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
