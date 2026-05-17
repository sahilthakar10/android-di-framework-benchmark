package com.codeint.shopapp.hilt.data.order.mapper

import com.codeint.shopapp.hilt.data.order.*
import com.codeint.shopapp.hilt.domain.order.*
import javax.inject.Inject

class OrderMapper @Inject constructor() {
    fun toDomain(entity: OrderEntity) = OrderDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<OrderEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: OrderDomainModel) = OrderEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: OrderResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
