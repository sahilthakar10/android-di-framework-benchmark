package com.codeint.shopapp.koin.data.cart.mapper

import com.codeint.shopapp.koin.data.cart.*
import com.codeint.shopapp.koin.domain.cart.*

class CartMapper constructor() {
    fun toDomain(entity: CartEntity): CartDomainModel = CartDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<CartEntity>): List<CartDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: CartDomainModel): CartEntity = CartEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: CartResponse): PagedResult<CartDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
