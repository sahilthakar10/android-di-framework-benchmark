package com.codeint.shopapp.hilt.data.cart.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.cart.*
import javax.inject.Inject

class CartRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: CartRequest) = CartResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = CartEntity(id, "Cart $id")
    fun create(entity: CartEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: CartEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = CartResponse(emptyList(), 0, page, false)
}
