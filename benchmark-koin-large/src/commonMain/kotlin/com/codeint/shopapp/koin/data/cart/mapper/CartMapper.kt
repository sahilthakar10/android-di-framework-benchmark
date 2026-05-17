package com.codeint.shopapp.koin.data.cart.mapper

import com.codeint.shopapp.koin.data.cart.*
import com.codeint.shopapp.koin.domain.cart.*

class CartMapper {
    fun toDomain(e: CartEntity) = CartDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<CartEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: CartDomainModel) = CartEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: CartResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
