package com.codeint.shopapp.hilt.data.order.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.order.*
import javax.inject.Inject

class OrderRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: OrderRequest) = OrderResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = OrderEntity(id, "Order $id")
    fun create(entity: OrderEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: OrderEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = OrderResponse(emptyList(), 0, page, false)
}
