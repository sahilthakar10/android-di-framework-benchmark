package com.codeint.shopapp.hilt.data.wishlist.mapper

import com.codeint.shopapp.hilt.data.wishlist.*
import com.codeint.shopapp.hilt.domain.wishlist.*
import javax.inject.Inject

class WishlistMapper @Inject constructor() {
    fun toDomain(entity: WishlistEntity) = WishlistDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<WishlistEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: WishlistDomainModel) = WishlistEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: WishlistResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
