package com.codeint.shopapp.metro.data.order.mapper

import com.codeint.shopapp.metro.data.order.*
import com.codeint.shopapp.metro.domain.order.*
import dev.zacsweers.metro.Inject

class OrderMapper @Inject constructor() {
    fun toDomain(e: OrderEntity) = OrderDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<OrderEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: OrderDomainModel) = OrderEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: OrderResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
