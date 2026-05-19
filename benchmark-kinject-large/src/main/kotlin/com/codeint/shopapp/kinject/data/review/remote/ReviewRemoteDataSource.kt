package com.codeint.shopapp.kinject.data.review.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.review.*
import me.tatarka.inject.annotations.Inject

@Inject class ReviewRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: ReviewRequest) = ReviewResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = ReviewEntity(id, "Review $id")
    fun create(e: ReviewEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: ReviewEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ReviewResponse(emptyList(), 0, page, false)
}
