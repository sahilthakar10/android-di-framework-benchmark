package com.codeint.shopapp.metro.data.feed.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.feed.*
import dev.zacsweers.metro.Inject

class FeedRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: FeedRequest) = FeedResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = FeedEntity(id, "Feed $id")
    fun create(e: FeedEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: FeedEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = FeedResponse(emptyList(), 0, page, false)
}
