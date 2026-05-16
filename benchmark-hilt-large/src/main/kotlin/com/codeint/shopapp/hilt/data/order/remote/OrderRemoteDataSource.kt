package com.codeint.shopapp.hilt.data.order.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.order.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: OrderRequest): OrderResponse {
        val headers = authInterceptor.intercept("/api/orders")
        return OrderResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): OrderEntity? {
        val headers = authInterceptor.intercept("/api/orders/$id")
        return OrderEntity(id, "Order $id")
    }

    fun create(entity: OrderEntity): OrderEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: OrderEntity): OrderEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): OrderResponse {
        if (rateLimiter.shouldThrottle("/api/orders/search")) return OrderResponse(emptyList(), 0, 0, false)
        return OrderResponse(emptyList(), 0, page, false)
    }
}
