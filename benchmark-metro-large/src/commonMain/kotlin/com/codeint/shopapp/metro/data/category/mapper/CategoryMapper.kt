package com.codeint.shopapp.metro.data.category.mapper

import com.codeint.shopapp.metro.data.category.*
import com.codeint.shopapp.metro.domain.category.*
import dev.zacsweers.metro.Inject

class CategoryMapper @Inject constructor() {
    fun toDomain(e: CategoryEntity) = CategoryDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<CategoryEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: CategoryDomainModel) = CategoryEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: CategoryResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
