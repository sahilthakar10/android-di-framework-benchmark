package com.codeint.shopapp.hilt.data.cart.mapper

import com.codeint.shopapp.hilt.data.cart.*
import com.codeint.shopapp.hilt.domain.cart.*
import javax.inject.Inject

class CartMapper @Inject constructor() {
    fun toDomain(entity: CartEntity) = CartDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<CartEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: CartDomainModel) = CartEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: CartResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
