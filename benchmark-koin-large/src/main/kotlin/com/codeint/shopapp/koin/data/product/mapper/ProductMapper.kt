package com.codeint.shopapp.koin.data.product.mapper

import com.codeint.shopapp.koin.data.product.*
import com.codeint.shopapp.koin.domain.product.*

class ProductMapper constructor() {
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
