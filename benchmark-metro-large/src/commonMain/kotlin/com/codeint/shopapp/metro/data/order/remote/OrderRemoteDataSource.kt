package com.codeint.shopapp.metro.data.order.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.order.*
import dev.zacsweers.metro.Inject

class OrderRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: OrderRequest) = OrderResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = OrderEntity(id, "Order $id")
    fun create(e: OrderEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: OrderEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = OrderResponse(emptyList(), 0, page, false)
}
