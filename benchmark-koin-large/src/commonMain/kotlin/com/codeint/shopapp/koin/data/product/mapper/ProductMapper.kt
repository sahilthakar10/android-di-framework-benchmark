package com.codeint.shopapp.koin.data.product.mapper

import com.codeint.shopapp.koin.data.product.*
import com.codeint.shopapp.koin.domain.product.*

class ProductMapper {
    fun toDomain(e: ProductEntity) = ProductDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<ProductEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: ProductDomainModel) = ProductEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: ProductResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
