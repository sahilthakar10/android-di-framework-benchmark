package com.codeint.shopapp.koin.data.search.remote

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.search.*

class SearchRemoteDataSource constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: SearchRequest): SearchResponse {
        val headers = authInterceptor.intercept("/api/searchs")
        return SearchResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): SearchEntity? {
        val headers = authInterceptor.intercept("/api/searchs/$id")
        return SearchEntity(id, "Search $id")
    }

    fun create(entity: SearchEntity): SearchEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: SearchEntity): SearchEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): SearchResponse {
        if (rateLimiter.shouldThrottle("/api/searchs/search")) return SearchResponse(emptyList(), 0, 0, false)
        return SearchResponse(emptyList(), 0, page, false)
    }
}
