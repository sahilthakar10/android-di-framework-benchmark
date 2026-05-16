package com.codeint.shopapp.koin.data.order.mapper

import com.codeint.shopapp.koin.data.order.*
import com.codeint.shopapp.koin.domain.order.*

class OrderMapper constructor() {
    fun toDomain(entity: OrderEntity): OrderDomainModel = OrderDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<OrderEntity>): List<OrderDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: OrderDomainModel): OrderEntity = OrderEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: OrderResponse): PagedResult<OrderDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
