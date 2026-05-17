package com.codeint.shopapp.metro.data.promotion.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.promotion.*
import dev.zacsweers.metro.Inject

class PromotionRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: PromotionRequest) = PromotionResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = PromotionEntity(id, "Promotion $id")
    fun create(e: PromotionEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: PromotionEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = PromotionResponse(emptyList(), 0, page, false)
}
