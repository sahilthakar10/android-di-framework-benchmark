package com.codeint.shopapp.metro.data.address.mapper

import com.codeint.shopapp.metro.data.address.*
import com.codeint.shopapp.metro.domain.address.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
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
