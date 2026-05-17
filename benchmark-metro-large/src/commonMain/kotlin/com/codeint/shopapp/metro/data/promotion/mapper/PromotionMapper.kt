package com.codeint.shopapp.metro.data.promotion.mapper

import com.codeint.shopapp.metro.data.promotion.*
import com.codeint.shopapp.metro.domain.promotion.*
import dev.zacsweers.metro.Inject

class PromotionMapper @Inject constructor() {
    fun toDomain(e: PromotionEntity) = PromotionDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<PromotionEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: PromotionDomainModel) = PromotionEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: PromotionResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
