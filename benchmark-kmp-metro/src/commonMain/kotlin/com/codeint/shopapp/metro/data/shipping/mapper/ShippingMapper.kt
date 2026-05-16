package com.codeint.shopapp.metro.data.shipping.mapper

import com.codeint.shopapp.metro.data.shipping.*
import com.codeint.shopapp.metro.domain.shipping.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ShippingMapper @Inject constructor() {
    fun toDomain(entity: ShippingEntity): ShippingDomainModel = ShippingDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<ShippingEntity>): List<ShippingDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: ShippingDomainModel): ShippingEntity = ShippingEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: ShippingResponse): PagedResult<ShippingDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
