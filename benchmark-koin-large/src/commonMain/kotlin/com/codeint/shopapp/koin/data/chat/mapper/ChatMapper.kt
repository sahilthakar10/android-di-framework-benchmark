package com.codeint.shopapp.koin.data.chat.mapper

import com.codeint.shopapp.koin.data.chat.*
import com.codeint.shopapp.koin.domain.chat.*

class ChatMapper {
    fun toDomain(e: ChatEntity) = ChatDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<ChatEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: ChatDomainModel) = ChatEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: ChatResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
