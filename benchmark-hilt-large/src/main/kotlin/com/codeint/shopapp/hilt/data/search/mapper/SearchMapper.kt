package com.codeint.shopapp.hilt.data.search.mapper

import com.codeint.shopapp.hilt.data.search.*
import com.codeint.shopapp.hilt.domain.search.*
import javax.inject.Inject

class SearchMapper @Inject constructor() {
    fun toDomain(entity: SearchEntity) = SearchDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<SearchEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: SearchDomainModel) = SearchEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: SearchResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
