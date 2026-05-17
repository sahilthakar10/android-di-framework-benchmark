package com.codeint.shopapp.hilt.data.user.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.user.*
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: UserRequest) = UserResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = UserEntity(id, "User $id")
    fun create(entity: UserEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: UserEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = UserResponse(emptyList(), 0, page, false)
}
