package com.codeint.shopapp.koin.data.order.mapper

import com.codeint.shopapp.koin.data.order.*
import com.codeint.shopapp.koin.domain.order.*

class OrderMapper {
    fun toDomain(e: OrderEntity) = OrderDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<OrderEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: OrderDomainModel) = OrderEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: OrderResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
