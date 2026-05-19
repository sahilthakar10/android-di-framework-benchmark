package com.codeint.shopapp.kinject.data.product.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.product.*
import me.tatarka.inject.annotations.Inject

@Inject class ProductRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: ProductRequest) = ProductResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = ProductEntity(id, "Product $id")
    fun create(e: ProductEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: ProductEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ProductResponse(emptyList(), 0, page, false)
}
