package com.codeint.shopapp.hilt.data.search.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.search.*
import javax.inject.Inject

class SearchRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: SearchRequest) = SearchResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = SearchEntity(id, "Search $id")
    fun create(entity: SearchEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: SearchEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = SearchResponse(emptyList(), 0, page, false)
}
