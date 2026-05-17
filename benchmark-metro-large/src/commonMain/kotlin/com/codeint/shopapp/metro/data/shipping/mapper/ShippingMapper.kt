package com.codeint.shopapp.metro.data.shipping.mapper

import com.codeint.shopapp.metro.data.shipping.*
import com.codeint.shopapp.metro.domain.shipping.*
import dev.zacsweers.metro.Inject

class ShippingMapper @Inject constructor() {
    fun toDomain(e: ShippingEntity) = ShippingDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<ShippingEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: ShippingDomainModel) = ShippingEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: ShippingResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
