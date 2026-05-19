package com.codeint.shopapp.kinject.data.category.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.category.*
import me.tatarka.inject.annotations.Inject

@Inject class CategoryRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: CategoryRequest) = CategoryResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = CategoryEntity(id, "Category $id")
    fun create(e: CategoryEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: CategoryEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = CategoryResponse(emptyList(), 0, page, false)
}
