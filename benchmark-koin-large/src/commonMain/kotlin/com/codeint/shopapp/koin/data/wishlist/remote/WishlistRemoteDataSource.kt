package com.codeint.shopapp.koin.data.wishlist.remote

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.wishlist.*

class WishlistRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: WishlistRequest) = WishlistResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = WishlistEntity(id, "Wishlist $id")
    fun create(e: WishlistEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: WishlistEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = WishlistResponse(emptyList(), 0, page, false)
}
