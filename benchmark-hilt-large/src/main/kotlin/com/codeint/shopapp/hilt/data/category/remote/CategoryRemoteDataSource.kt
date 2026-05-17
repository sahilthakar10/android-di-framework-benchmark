package com.codeint.shopapp.hilt.data.category.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.category.*
import javax.inject.Inject

class CategoryRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: CategoryRequest) = CategoryResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = CategoryEntity(id, "Category $id")
    fun create(entity: CategoryEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: CategoryEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = CategoryResponse(emptyList(), 0, page, false)
}
