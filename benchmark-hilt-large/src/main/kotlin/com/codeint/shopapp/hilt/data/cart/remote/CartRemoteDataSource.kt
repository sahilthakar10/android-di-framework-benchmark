package com.codeint.shopapp.hilt.data.cart.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.cart.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: CartRequest): CartResponse {
        val headers = authInterceptor.intercept("/api/carts")
        return CartResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): CartEntity? {
        val headers = authInterceptor.intercept("/api/carts/$id")
        return CartEntity(id, "Cart $id")
    }

    fun create(entity: CartEntity): CartEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: CartEntity): CartEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): CartResponse {
        if (rateLimiter.shouldThrottle("/api/carts/search")) return CartResponse(emptyList(), 0, 0, false)
        return CartResponse(emptyList(), 0, page, false)
    }
}
