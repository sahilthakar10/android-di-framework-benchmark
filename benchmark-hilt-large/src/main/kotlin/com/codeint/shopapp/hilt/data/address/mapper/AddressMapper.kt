package com.codeint.shopapp.hilt.data.address.mapper

import com.codeint.shopapp.hilt.data.address.*
import com.codeint.shopapp.hilt.domain.address.*
import javax.inject.Inject

class AddressMapper @Inject constructor() {
    fun toDomain(entity: AddressEntity) = AddressDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<AddressEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: AddressDomainModel) = AddressEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: AddressResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
