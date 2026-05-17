package com.codeint.shopapp.hilt.data.review.mapper

import com.codeint.shopapp.hilt.data.review.*
import com.codeint.shopapp.hilt.domain.review.*
import javax.inject.Inject

class ReviewMapper @Inject constructor() {
    fun toDomain(entity: ReviewEntity) = ReviewDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<ReviewEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: ReviewDomainModel) = ReviewEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: ReviewResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
