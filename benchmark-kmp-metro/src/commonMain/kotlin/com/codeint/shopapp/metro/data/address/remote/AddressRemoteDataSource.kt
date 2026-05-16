package com.codeint.shopapp.metro.data.address.remote

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.address.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class AddressRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: AddressRequest): AddressResponse {
        val headers = authInterceptor.intercept("/api/addresss")
        return AddressResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): AddressEntity? {
        val headers = authInterceptor.intercept("/api/addresss/$id")
        return AddressEntity(id, "Address $id")
    }

    fun create(entity: AddressEntity): AddressEntity = entity.copy(id = "new_${com.codeint.shopapp.common.platform.currentTimeMillis()}")

    fun update(id: String, entity: AddressEntity): AddressEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): AddressResponse {
        if (rateLimiter.shouldThrottle("/api/addresss/search")) return AddressResponse(emptyList(), 0, 0, false)
        return AddressResponse(emptyList(), 0, page, false)
    }
}
