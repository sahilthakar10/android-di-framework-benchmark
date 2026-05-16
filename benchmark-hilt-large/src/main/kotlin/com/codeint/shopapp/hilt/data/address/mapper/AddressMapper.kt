package com.codeint.shopapp.hilt.data.address.mapper

import com.codeint.shopapp.hilt.data.address.*
import com.codeint.shopapp.hilt.domain.address.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressMapper @Inject constructor() {
    fun toDomain(entity: AddressEntity): AddressDomainModel = AddressDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<AddressEntity>): List<AddressDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: AddressDomainModel): AddressEntity = AddressEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: AddressResponse): PagedResult<AddressDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
