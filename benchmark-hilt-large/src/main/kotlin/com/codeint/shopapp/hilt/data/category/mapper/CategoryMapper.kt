package com.codeint.shopapp.hilt.data.category.mapper

import com.codeint.shopapp.hilt.data.category.*
import com.codeint.shopapp.hilt.domain.category.*
import javax.inject.Inject

class CategoryMapper @Inject constructor() {
    fun toDomain(entity: CategoryEntity) = CategoryDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<CategoryEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: CategoryDomainModel) = CategoryEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: CategoryResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
