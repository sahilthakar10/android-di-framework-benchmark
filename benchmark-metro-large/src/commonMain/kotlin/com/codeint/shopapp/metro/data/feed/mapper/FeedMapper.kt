package com.codeint.shopapp.metro.data.feed.mapper

import com.codeint.shopapp.metro.data.feed.*
import com.codeint.shopapp.metro.domain.feed.*
import dev.zacsweers.metro.Inject

class FeedMapper @Inject constructor() {
    fun toDomain(e: FeedEntity) = FeedDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<FeedEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: FeedDomainModel) = FeedEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: FeedResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
