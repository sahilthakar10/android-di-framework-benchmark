package com.codeint.shopapp.kinject.data.search.mapper

import com.codeint.shopapp.kinject.data.search.*
import com.codeint.shopapp.kinject.domain.search.*
import me.tatarka.inject.annotations.Inject

@Inject class SearchMapper {
    fun toDomain(e: SearchEntity) = SearchDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<SearchEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: SearchDomainModel) = SearchEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: SearchResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
