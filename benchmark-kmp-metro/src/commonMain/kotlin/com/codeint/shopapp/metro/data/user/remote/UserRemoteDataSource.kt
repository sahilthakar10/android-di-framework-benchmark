package com.codeint.shopapp.metro.data.user.remote

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.user.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class UserRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: UserRequest): UserResponse {
        val headers = authInterceptor.intercept("/api/users")
        return UserResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): UserEntity? {
        val headers = authInterceptor.intercept("/api/users/$id")
        return UserEntity(id, "User $id")
    }

    fun create(entity: UserEntity): UserEntity = entity.copy(id = "new_${com.codeint.shopapp.common.platform.currentTimeMillis()}")

    fun update(id: String, entity: UserEntity): UserEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): UserResponse {
        if (rateLimiter.shouldThrottle("/api/users/search")) return UserResponse(emptyList(), 0, 0, false)
        return UserResponse(emptyList(), 0, page, false)
    }
}
