package com.codeint.shopapp.metro.data.shipping.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.shipping.*
import dev.zacsweers.metro.Inject

class ShippingRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: ShippingRequest) = ShippingResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = ShippingEntity(id, "Shipping $id")
    fun create(e: ShippingEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: ShippingEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ShippingResponse(emptyList(), 0, page, false)
}
