package com.codeint.shopapp.hilt.data.chat.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.chat.*
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: ChatRequest) = ChatResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = ChatEntity(id, "Chat $id")
    fun create(entity: ChatEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: ChatEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ChatResponse(emptyList(), 0, page, false)
}
