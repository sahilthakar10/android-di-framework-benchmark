package com.codeint.shopapp.hilt.data.shipping.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.shipping.*
import javax.inject.Inject

class ShippingRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: ShippingRequest) = ShippingResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = ShippingEntity(id, "Shipping $id")
    fun create(entity: ShippingEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: ShippingEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ShippingResponse(emptyList(), 0, page, false)
}
