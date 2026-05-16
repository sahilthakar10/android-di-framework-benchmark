package com.codeint.shopapp.koin.data.review.remote

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.review.*

class ReviewRemoteDataSource constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: ReviewRequest): ReviewResponse {
        val headers = authInterceptor.intercept("/api/reviews")
        return ReviewResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): ReviewEntity? {
        val headers = authInterceptor.intercept("/api/reviews/$id")
        return ReviewEntity(id, "Review $id")
    }

    fun create(entity: ReviewEntity): ReviewEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: ReviewEntity): ReviewEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): ReviewResponse {
        if (rateLimiter.shouldThrottle("/api/reviews/search")) return ReviewResponse(emptyList(), 0, 0, false)
        return ReviewResponse(emptyList(), 0, page, false)
    }
}
