package com.codeint.shopapp.metro.data.wishlist.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.wishlist.*
import dev.zacsweers.metro.Inject

class WishlistRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: WishlistRequest) = WishlistResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = WishlistEntity(id, "Wishlist $id")
    fun create(e: WishlistEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: WishlistEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = WishlistResponse(emptyList(), 0, page, false)
}
