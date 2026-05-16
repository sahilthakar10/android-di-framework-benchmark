package com.codeint.shopapp.metro.data.shipping.remote

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.shipping.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ShippingRemoteDataSource @Inject constructor(
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

    fun create(entity: ShippingEntity): ShippingEntity = entity.copy(id = "new_${com.codeint.shopapp.common.platform.currentTimeMillis()}")

    fun update(id: String, entity: ShippingEntity): ShippingEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): ShippingResponse {
        if (rateLimiter.shouldThrottle("/api/shippings/search")) return ShippingResponse(emptyList(), 0, 0, false)
        return ShippingResponse(emptyList(), 0, page, false)
    }
}
