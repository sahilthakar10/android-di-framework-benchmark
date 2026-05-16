package com.codeint.shopapp.koin.data.wishlist.remote

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.wishlist.*

class WishlistRemoteDataSource constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: WishlistRequest): WishlistResponse {
        val headers = authInterceptor.intercept("/api/wishlists")
        return WishlistResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): WishlistEntity? {
        val headers = authInterceptor.intercept("/api/wishlists/$id")
        return WishlistEntity(id, "Wishlist $id")
    }

    fun create(entity: WishlistEntity): WishlistEntity = entity.copy(id = "new_${com.codeint.shopapp.common.platform.currentTimeMillis()}")

    fun update(id: String, entity: WishlistEntity): WishlistEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): WishlistResponse {
        if (rateLimiter.shouldThrottle("/api/wishlists/search")) return WishlistResponse(emptyList(), 0, 0, false)
        return WishlistResponse(emptyList(), 0, page, false)
    }
}
