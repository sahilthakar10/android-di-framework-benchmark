package com.codeint.shopapp.koin.data.order.remote

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.order.*

class OrderRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: OrderRequest) = OrderResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = OrderEntity(id, "Order $id")
    fun create(e: OrderEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: OrderEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = OrderResponse(emptyList(), 0, page, false)
}
