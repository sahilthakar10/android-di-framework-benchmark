package com.codeint.shopapp.metro.data.address.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.address.*
import dev.zacsweers.metro.Inject

class AddressRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: AddressRequest) = AddressResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = AddressEntity(id, "Address $id")
    fun create(e: AddressEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: AddressEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = AddressResponse(emptyList(), 0, page, false)
}
