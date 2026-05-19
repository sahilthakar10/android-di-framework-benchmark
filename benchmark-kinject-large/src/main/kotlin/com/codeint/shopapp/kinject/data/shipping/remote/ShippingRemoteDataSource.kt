package com.codeint.shopapp.kinject.data.shipping.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.shipping.*
import me.tatarka.inject.annotations.Inject

@Inject class ShippingRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: ShippingRequest) = ShippingResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = ShippingEntity(id, "Shipping $id")
    fun create(e: ShippingEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: ShippingEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ShippingResponse(emptyList(), 0, page, false)
}
