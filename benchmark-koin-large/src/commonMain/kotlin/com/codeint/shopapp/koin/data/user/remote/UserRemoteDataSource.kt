package com.codeint.shopapp.koin.data.user.remote

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.user.*

class UserRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: UserRequest) = UserResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = UserEntity(id, "User $id")
    fun create(e: UserEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: UserEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = UserResponse(emptyList(), 0, page, false)
}
