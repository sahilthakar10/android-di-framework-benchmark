package com.codeint.shopapp.hilt.data.feed.mapper

import com.codeint.shopapp.hilt.data.feed.*
import com.codeint.shopapp.hilt.domain.feed.*
import javax.inject.Inject

class FeedMapper @Inject constructor() {
    fun toDomain(entity: FeedEntity) = FeedDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<FeedEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: FeedDomainModel) = FeedEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: FeedResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
