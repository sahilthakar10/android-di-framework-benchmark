package com.codeint.shopapp.koin.data.feed.remote

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.feed.*

class FeedRemoteDataSource constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: FeedRequest): FeedResponse {
        val headers = authInterceptor.intercept("/api/feeds")
        return FeedResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): FeedEntity? {
        val headers = authInterceptor.intercept("/api/feeds/$id")
        return FeedEntity(id, "Feed $id")
    }

    fun create(entity: FeedEntity): FeedEntity = entity.copy(id = "new_${com.codeint.shopapp.common.platform.currentTimeMillis()}")

    fun update(id: String, entity: FeedEntity): FeedEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): FeedResponse {
        if (rateLimiter.shouldThrottle("/api/feeds/search")) return FeedResponse(emptyList(), 0, 0, false)
        return FeedResponse(emptyList(), 0, page, false)
    }
}
