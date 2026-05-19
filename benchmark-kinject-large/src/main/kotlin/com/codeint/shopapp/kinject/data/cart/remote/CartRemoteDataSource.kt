package com.codeint.shopapp.kinject.data.cart.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.cart.*
import me.tatarka.inject.annotations.Inject

@Inject class CartRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: CartRequest) = CartResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = CartEntity(id, "Cart $id")
    fun create(e: CartEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: CartEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = CartResponse(emptyList(), 0, page, false)
}
