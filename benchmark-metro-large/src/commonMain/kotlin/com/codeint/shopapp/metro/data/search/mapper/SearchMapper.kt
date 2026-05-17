package com.codeint.shopapp.metro.data.search.mapper

import com.codeint.shopapp.metro.data.search.*
import com.codeint.shopapp.metro.domain.search.*
import dev.zacsweers.metro.Inject

class SearchMapper @Inject constructor() {
    fun toDomain(e: SearchEntity) = SearchDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<SearchEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: SearchDomainModel) = SearchEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: SearchResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
