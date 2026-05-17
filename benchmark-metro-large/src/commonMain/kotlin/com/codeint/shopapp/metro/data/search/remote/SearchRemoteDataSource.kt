package com.codeint.shopapp.metro.data.search.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.search.*
import dev.zacsweers.metro.Inject

class SearchRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: SearchRequest) = SearchResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = SearchEntity(id, "Search $id")
    fun create(e: SearchEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: SearchEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = SearchResponse(emptyList(), 0, page, false)
}
