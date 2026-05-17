package com.codeint.shopapp.koin.data.chat

import com.codeint.shopapp.koin.domain.chat.*

interface ChatRepository {
    fun getAll(request: ChatRequest = ChatRequest()): PagedResult<ChatDomainModel>
    fun getById(id: String): ChatDomainModel?
    fun create(model: ChatDomainModel): ChatDomainModel
    fun update(id: String, model: ChatDomainModel): ChatDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<ChatDomainModel>
    fun clearCache()
}
