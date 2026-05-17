package com.codeint.shopapp.koin.data.feed.mapper

import com.codeint.shopapp.koin.data.feed.*
import com.codeint.shopapp.koin.domain.feed.*

class FeedMapper {
    fun toDomain(e: FeedEntity) = FeedDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<FeedEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: FeedDomainModel) = FeedEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: FeedResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
