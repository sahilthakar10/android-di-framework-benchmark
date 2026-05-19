package com.codeint.shopapp.kinject.data.user.remote

import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.data.user.*
import me.tatarka.inject.annotations.Inject

@Inject class UserRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: UserRequest) = UserResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = UserEntity(id, "User $id")
    fun create(e: UserEntity) = e.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, e: UserEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = UserResponse(emptyList(), 0, page, false)
}
