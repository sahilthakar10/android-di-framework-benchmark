package com.codeint.shopapp.hilt.data.shipping.mapper

import com.codeint.shopapp.hilt.data.shipping.*
import com.codeint.shopapp.hilt.domain.shipping.*
import javax.inject.Inject

class ShippingMapper @Inject constructor() {
    fun toDomain(entity: ShippingEntity) = ShippingDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<ShippingEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: ShippingDomainModel) = ShippingEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: ShippingResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
