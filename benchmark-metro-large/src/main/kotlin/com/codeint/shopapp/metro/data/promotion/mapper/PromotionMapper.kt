package com.codeint.shopapp.metro.data.promotion.mapper

import com.codeint.shopapp.metro.data.promotion.*
import com.codeint.shopapp.metro.domain.promotion.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class PromotionMapper @Inject constructor() {
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
