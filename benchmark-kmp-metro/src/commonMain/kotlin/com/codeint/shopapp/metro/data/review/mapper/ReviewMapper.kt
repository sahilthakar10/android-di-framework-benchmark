package com.codeint.shopapp.metro.data.review.mapper

import com.codeint.shopapp.metro.data.review.*
import com.codeint.shopapp.metro.domain.review.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ReviewMapper @Inject constructor() {
    fun toDomain(entity: ReviewEntity): ReviewDomainModel = ReviewDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<ReviewEntity>): List<ReviewDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: ReviewDomainModel): ReviewEntity = ReviewEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: ReviewResponse): PagedResult<ReviewDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
