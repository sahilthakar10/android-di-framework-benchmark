package com.codeint.shopapp.hilt.data.category.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.category.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRemoteDataSource @Inject constructor(
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

    fun create(entity: CategoryEntity): CategoryEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: CategoryEntity): CategoryEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): CategoryResponse {
        if (rateLimiter.shouldThrottle("/api/categorys/search")) return CategoryResponse(emptyList(), 0, 0, false)
        return CategoryResponse(emptyList(), 0, page, false)
    }
}
