package com.codeint.shopapp.kinject.data.address.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.address.*
import me.tatarka.inject.annotations.Inject

@Inject class AddressRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: AddressRequest) = AddressResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = AddressEntity(id, "Address $id")
    fun create(e: AddressEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: AddressEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = AddressResponse(emptyList(), 0, page, false)
}
