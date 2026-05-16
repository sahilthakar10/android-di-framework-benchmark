package com.codeint.shopapp.metro.data.product.remote

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.product.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ProductRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: ProductRequest): ProductResponse {
        val headers = authInterceptor.intercept("/api/products")
        return ProductResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): ProductEntity? {
        val headers = authInterceptor.intercept("/api/products/$id")
        return ProductEntity(id, "Product $id")
    }

    fun create(entity: ProductEntity): ProductEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: ProductEntity): ProductEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): ProductResponse {
        if (rateLimiter.shouldThrottle("/api/products/search")) return ProductResponse(emptyList(), 0, 0, false)
        return ProductResponse(emptyList(), 0, page, false)
    }
}
