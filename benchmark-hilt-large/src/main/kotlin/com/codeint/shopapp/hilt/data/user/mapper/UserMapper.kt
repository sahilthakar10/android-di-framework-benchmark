package com.codeint.shopapp.hilt.data.user.mapper

import com.codeint.shopapp.hilt.data.user.*
import com.codeint.shopapp.hilt.domain.user.*
import javax.inject.Inject

class UserMapper @Inject constructor() {
    fun toDomain(entity: UserEntity) = UserDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )
    fun toDomainList(entities: List<UserEntity>) = entities.map { toDomain(it) }
    fun toEntity(domain: UserDomainModel) = UserEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )
    fun toPagedResult(response: UserResponse) = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
