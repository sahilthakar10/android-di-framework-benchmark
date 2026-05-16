package com.codeint.shopapp.metro.data.order.remote

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.order.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
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

    fun create(entity: OrderEntity): OrderEntity = entity.copy(id = "new_${com.codeint.shopapp.common.platform.currentTimeMillis()}")

    fun update(id: String, entity: OrderEntity): OrderEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): OrderResponse {
        if (rateLimiter.shouldThrottle("/api/orders/search")) return OrderResponse(emptyList(), 0, 0, false)
        return OrderResponse(emptyList(), 0, page, false)
    }
}
