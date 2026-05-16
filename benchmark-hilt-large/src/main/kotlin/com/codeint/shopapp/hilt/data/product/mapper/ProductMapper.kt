package com.codeint.shopapp.hilt.data.product.mapper

import com.codeint.shopapp.hilt.data.product.*
import com.codeint.shopapp.hilt.domain.product.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductMapper @Inject constructor() {
    fun toDomain(entity: ProductEntity): ProductDomainModel = ProductDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<ProductEntity>): List<ProductDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: ProductDomainModel): ProductEntity = ProductEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: ProductResponse): PagedResult<ProductDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
