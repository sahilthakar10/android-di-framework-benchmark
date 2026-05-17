package com.codeint.shopapp.hilt.data.product.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.product.*
import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: ProductRequest) = ProductResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = ProductEntity(id, "Product $id")
    fun create(entity: ProductEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: ProductEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = ProductResponse(emptyList(), 0, page, false)
}
