package com.codeint.shopapp.kinject.data.chat.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.chat.*
import me.tatarka.inject.annotations.Inject

@Inject class ChatRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: ChatRequest) = ChatResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = ChatEntity(id, "Chat $id")
    fun create(e: ChatEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: ChatEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ChatResponse(emptyList(), 0, page, false)
}
