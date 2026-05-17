package com.codeint.shopapp.hilt.data.promotion.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.promotion.*
import javax.inject.Inject

class PromotionRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: PromotionRequest) = PromotionResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = PromotionEntity(id, "Promotion $id")
    fun create(entity: PromotionEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: PromotionEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = PromotionResponse(emptyList(), 0, page, false)
}
