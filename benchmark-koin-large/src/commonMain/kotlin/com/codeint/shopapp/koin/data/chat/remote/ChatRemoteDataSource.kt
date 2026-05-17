package com.codeint.shopapp.koin.data.chat.remote

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.chat.*

class ChatRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: ChatRequest) = ChatResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = ChatEntity(id, "Chat $id")
    fun create(e: ChatEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: ChatEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ChatResponse(emptyList(), 0, page, false)
}
