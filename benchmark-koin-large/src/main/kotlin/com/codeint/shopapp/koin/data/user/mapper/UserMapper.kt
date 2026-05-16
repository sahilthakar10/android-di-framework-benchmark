package com.codeint.shopapp.koin.data.user.mapper

import com.codeint.shopapp.koin.data.user.*
import com.codeint.shopapp.koin.domain.user.*

class UserMapper constructor() {
    fun toDomain(entity: UserEntity): UserDomainModel = UserDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<UserEntity>): List<UserDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: UserDomainModel): UserEntity = UserEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: UserResponse): PagedResult<UserDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
