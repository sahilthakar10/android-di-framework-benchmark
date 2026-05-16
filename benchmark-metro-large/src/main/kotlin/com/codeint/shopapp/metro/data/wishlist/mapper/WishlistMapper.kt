package com.codeint.shopapp.metro.data.wishlist.mapper

import com.codeint.shopapp.metro.data.wishlist.*
import com.codeint.shopapp.metro.domain.wishlist.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class WishlistMapper @Inject constructor() {
    fun toDomain(entity: WishlistEntity): WishlistDomainModel = WishlistDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<WishlistEntity>): List<WishlistDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: WishlistDomainModel): WishlistEntity = WishlistEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: WishlistResponse): PagedResult<WishlistDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
