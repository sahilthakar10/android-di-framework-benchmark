package com.codeint.shopapp.koin.data.chat.remote

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.chat.*

class ChatRemoteDataSource constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: ChatRequest): ChatResponse {
        val headers = authInterceptor.intercept("/api/chats")
        return ChatResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): ChatEntity? {
        val headers = authInterceptor.intercept("/api/chats/$id")
        return ChatEntity(id, "Chat $id")
    }

    fun create(entity: ChatEntity): ChatEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: ChatEntity): ChatEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): ChatResponse {
        if (rateLimiter.shouldThrottle("/api/chats/search")) return ChatResponse(emptyList(), 0, 0, false)
        return ChatResponse(emptyList(), 0, page, false)
    }
}
