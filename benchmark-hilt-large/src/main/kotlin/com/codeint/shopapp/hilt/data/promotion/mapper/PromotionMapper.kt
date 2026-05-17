package com.codeint.shopapp.hilt.data.promotion.mapper

import com.codeint.shopapp.hilt.data.promotion.*
import com.codeint.shopapp.hilt.domain.promotion.*
import javax.inject.Inject

class PromotionMapper @Inject constructor() {
    fun toDomain(entity: PromotionEntity) = PromotionDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<PromotionEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: PromotionDomainModel) = PromotionEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: PromotionResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
