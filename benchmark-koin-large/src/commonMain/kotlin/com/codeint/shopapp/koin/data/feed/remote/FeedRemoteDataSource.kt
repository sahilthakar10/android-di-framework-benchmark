package com.codeint.shopapp.koin.data.feed.remote

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.feed.*

class FeedRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: FeedRequest) = FeedResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = FeedEntity(id, "Feed $id")
    fun create(e: FeedEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: FeedEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = FeedResponse(emptyList(), 0, page, false)
}
