package com.codeint.shopapp.koin.data.category.remote

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.category.*

class CategoryRemoteDataSource constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: CategoryRequest): CategoryResponse {
        val headers = authInterceptor.intercept("/api/categorys")
        return CategoryResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): CategoryEntity? {
        val headers = authInterceptor.intercept("/api/categorys/$id")
        return CategoryEntity(id, "Category $id")
    }

    fun create(entity: CategoryEntity): CategoryEntity = entity.copy(id = "new_${com.codeint.shopapp.common.platform.currentTimeMillis()}")

    fun update(id: String, entity: CategoryEntity): CategoryEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): CategoryResponse {
        if (rateLimiter.shouldThrottle("/api/categorys/search")) return CategoryResponse(emptyList(), 0, 0, false)
        return CategoryResponse(emptyList(), 0, page, false)
    }
}
