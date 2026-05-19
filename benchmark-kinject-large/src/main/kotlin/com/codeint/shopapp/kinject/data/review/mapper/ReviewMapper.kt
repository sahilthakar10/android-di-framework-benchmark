package com.codeint.shopapp.kinject.data.review.mapper

import com.codeint.shopapp.kinject.data.review.*
import com.codeint.shopapp.kinject.domain.review.*
import me.tatarka.inject.annotations.Inject

@Inject class ReviewMapper {
    fun toDomain(e: ReviewEntity) = ReviewDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<ReviewEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: ReviewDomainModel) = ReviewEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: ReviewResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
