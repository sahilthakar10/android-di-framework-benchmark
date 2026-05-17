package com.codeint.shopapp.koin.data.product.remote

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.product.*

class ProductRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: ProductRequest) = ProductResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = ProductEntity(id, "Product $id")
    fun create(e: ProductEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: ProductEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ProductResponse(emptyList(), 0, page, false)
}
