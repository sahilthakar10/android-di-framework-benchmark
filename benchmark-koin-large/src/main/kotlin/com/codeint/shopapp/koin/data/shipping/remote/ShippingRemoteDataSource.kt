package com.codeint.shopapp.koin.data.shipping.remote

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.shipping.*

class ShippingRemoteDataSource constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: ShippingRequest): ShippingResponse {
        val headers = authInterceptor.intercept("/api/shippings")
        return ShippingResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): ShippingEntity? {
        val headers = authInterceptor.intercept("/api/shippings/$id")
        return ShippingEntity(id, "Shipping $id")
    }

    fun create(entity: ShippingEntity): ShippingEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: ShippingEntity): ShippingEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): ShippingResponse {
        if (rateLimiter.shouldThrottle("/api/shippings/search")) return ShippingResponse(emptyList(), 0, 0, false)
        return ShippingResponse(emptyList(), 0, page, false)
    }
}
