package com.codeint.shopapp.metro.data.chat.mapper

import com.codeint.shopapp.metro.data.chat.*
import com.codeint.shopapp.metro.domain.chat.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ChatMapper @Inject constructor() {
    fun toDomain(entity: ChatEntity): ChatDomainModel = ChatDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<ChatEntity>): List<ChatDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: ChatDomainModel): ChatEntity = ChatEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: ChatResponse): PagedResult<ChatDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
