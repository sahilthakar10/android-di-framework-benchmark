package com.codeint.shopapp.hilt.data.review.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.review.*
import javax.inject.Inject

class ReviewRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: ReviewRequest) = ReviewResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = ReviewEntity(id, "Review $id")
    fun create(entity: ReviewEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: ReviewEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ReviewResponse(emptyList(), 0, page, false)
}
