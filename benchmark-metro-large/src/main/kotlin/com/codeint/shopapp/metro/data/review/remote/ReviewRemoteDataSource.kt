package com.codeint.shopapp.metro.data.review.remote

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.review.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ReviewRemoteDataSource @Inject constructor(
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
