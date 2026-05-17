package com.codeint.shopapp.hilt.data.wishlist.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.wishlist.*
import javax.inject.Inject

class WishlistRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: WishlistRequest) = WishlistResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = WishlistEntity(id, "Wishlist $id")
    fun create(entity: WishlistEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: WishlistEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = WishlistResponse(emptyList(), 0, page, false)
}
