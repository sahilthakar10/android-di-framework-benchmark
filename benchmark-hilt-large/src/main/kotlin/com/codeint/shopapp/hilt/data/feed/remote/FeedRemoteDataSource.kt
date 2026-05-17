package com.codeint.shopapp.hilt.data.feed.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.feed.*
import javax.inject.Inject

class FeedRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: FeedRequest) = FeedResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = FeedEntity(id, "Feed $id")
    fun create(entity: FeedEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: FeedEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = FeedResponse(emptyList(), 0, page, false)
}
