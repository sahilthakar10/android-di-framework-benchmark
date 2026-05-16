package com.codeint.shopapp.hilt.data.chat.mapper

import com.codeint.shopapp.hilt.data.chat.*
import com.codeint.shopapp.hilt.domain.chat.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
