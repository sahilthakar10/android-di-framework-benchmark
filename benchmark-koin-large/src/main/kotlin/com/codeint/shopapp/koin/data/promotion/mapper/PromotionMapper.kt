package com.codeint.shopapp.koin.data.promotion.mapper

import com.codeint.shopapp.koin.data.promotion.*
import com.codeint.shopapp.koin.domain.promotion.*

class PromotionMapper constructor() {
    fun toDomain(entity: PromotionEntity): PromotionDomainModel = PromotionDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<PromotionEntity>): List<PromotionDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: PromotionDomainModel): PromotionEntity = PromotionEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: PromotionResponse): PagedResult<PromotionDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
