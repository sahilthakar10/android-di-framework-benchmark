package com.codeint.shopapp.koin.data.user.mapper

import com.codeint.shopapp.koin.data.user.*
import com.codeint.shopapp.koin.domain.user.*

class UserMapper {
    fun toDomain(e: UserEntity) = UserDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<UserEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: UserDomainModel) = UserEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: UserResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
