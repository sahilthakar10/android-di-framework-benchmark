package com.codeint.shopapp.koin.data.search.mapper

import com.codeint.shopapp.koin.data.search.*
import com.codeint.shopapp.koin.domain.search.*

class SearchMapper constructor() {
    fun toDomain(entity: SearchEntity): SearchDomainModel = SearchDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<SearchEntity>): List<SearchDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: SearchDomainModel): SearchEntity = SearchEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: SearchResponse): PagedResult<SearchDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
