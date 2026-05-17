package com.codeint.shopapp.hilt.data.address.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.address.*
import javax.inject.Inject

class AddressRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: AddressRequest) = AddressResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = AddressEntity(id, "Address $id")
    fun create(entity: AddressEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: AddressEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = AddressResponse(emptyList(), 0, page, false)
}
