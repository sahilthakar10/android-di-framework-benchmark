package com.codeint.shopapp.metro.data.promotion.remote

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.promotion.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class PromotionRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: PromotionRequest): PromotionResponse {
        val headers = authInterceptor.intercept("/api/promotions")
        return PromotionResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): PromotionEntity? {
        val headers = authInterceptor.intercept("/api/promotions/$id")
        return PromotionEntity(id, "Promotion $id")
    }

    fun create(entity: PromotionEntity): PromotionEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: PromotionEntity): PromotionEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): PromotionResponse {
        if (rateLimiter.shouldThrottle("/api/promotions/search")) return PromotionResponse(emptyList(), 0, 0, false)
        return PromotionResponse(emptyList(), 0, page, false)
    }
}
