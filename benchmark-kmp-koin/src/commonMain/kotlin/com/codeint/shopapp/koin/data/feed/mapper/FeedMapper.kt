package com.codeint.shopapp.koin.data.feed.mapper

import com.codeint.shopapp.koin.data.feed.*
import com.codeint.shopapp.koin.domain.feed.*

class FeedMapper constructor() {
    fun toDomain(entity: FeedEntity): FeedDomainModel = FeedDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<FeedEntity>): List<FeedDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: FeedDomainModel): FeedEntity = FeedEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: FeedResponse): PagedResult<FeedDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
