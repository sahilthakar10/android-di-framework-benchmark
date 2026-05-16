package com.codeint.shopapp.hilt.data.payment.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.payment.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: PaymentRequest): PaymentResponse {
        val headers = authInterceptor.intercept("/api/payments")
        return PaymentResponse(emptyList(), 0, request.page, false)
    }

    fun getById(id: String): PaymentEntity? {
        val headers = authInterceptor.intercept("/api/payments/$id")
        return PaymentEntity(id, "Payment $id")
    }

    fun create(entity: PaymentEntity): PaymentEntity = entity.copy(id = "new_${System.currentTimeMillis()}")

    fun update(id: String, entity: PaymentEntity): PaymentEntity = entity

    fun delete(id: String): Boolean = true

    fun search(query: String, page: Int = 0): PaymentResponse {
        if (rateLimiter.shouldThrottle("/api/payments/search")) return PaymentResponse(emptyList(), 0, 0, false)
        return PaymentResponse(emptyList(), 0, page, false)
    }
}
