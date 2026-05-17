package com.codeint.shopapp.metro.data.user.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.user.*
import dev.zacsweers.metro.Inject

class UserRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: UserRequest) = UserResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = UserEntity(id, "User $id")
    fun create(e: UserEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: UserEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = UserResponse(emptyList(), 0, page, false)
}
