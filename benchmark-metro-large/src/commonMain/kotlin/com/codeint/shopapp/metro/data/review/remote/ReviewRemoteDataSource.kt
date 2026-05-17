package com.codeint.shopapp.metro.data.review.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.review.*
import dev.zacsweers.metro.Inject

class ReviewRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: ReviewRequest) = ReviewResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = ReviewEntity(id, "Review $id")
    fun create(e: ReviewEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: ReviewEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ReviewResponse(emptyList(), 0, page, false)
}
