package com.codeint.shopapp.koin.data.category.mapper

import com.codeint.shopapp.koin.data.category.*
import com.codeint.shopapp.koin.domain.category.*

class CategoryMapper constructor() {
    fun toDomain(entity: CategoryEntity): CategoryDomainModel = CategoryDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<CategoryEntity>): List<CategoryDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: CategoryDomainModel): CategoryEntity = CategoryEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: CategoryResponse): PagedResult<CategoryDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
