package com.codeint.shopapp.metro.data.address.mapper

import com.codeint.shopapp.metro.data.address.*
import com.codeint.shopapp.metro.domain.address.*
import dev.zacsweers.metro.Inject

class AddressMapper @Inject constructor() {
    fun toDomain(e: AddressEntity) = AddressDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<AddressEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: AddressDomainModel) = AddressEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: AddressResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
