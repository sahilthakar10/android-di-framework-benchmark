package com.codeint.shopapp.metro.data.user.mapper

import com.codeint.shopapp.metro.data.user.*
import com.codeint.shopapp.metro.domain.user.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class UserMapper @Inject constructor() {
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
