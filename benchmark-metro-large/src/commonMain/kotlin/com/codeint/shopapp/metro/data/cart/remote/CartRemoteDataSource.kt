package com.codeint.shopapp.metro.data.cart.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.cart.*
import dev.zacsweers.metro.Inject

class CartRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: CartRequest) = CartResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = CartEntity(id, "Cart $id")
    fun create(e: CartEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: CartEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = CartResponse(emptyList(), 0, page, false)
}
