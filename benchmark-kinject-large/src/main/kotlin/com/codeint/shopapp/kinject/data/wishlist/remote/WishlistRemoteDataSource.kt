package com.codeint.shopapp.kinject.data.wishlist.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.wishlist.*
import me.tatarka.inject.annotations.Inject

@Inject class WishlistRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: WishlistRequest) = WishlistResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = WishlistEntity(id, "Wishlist $id")
    fun create(e: WishlistEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: WishlistEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = WishlistResponse(emptyList(), 0, page, false)
}
