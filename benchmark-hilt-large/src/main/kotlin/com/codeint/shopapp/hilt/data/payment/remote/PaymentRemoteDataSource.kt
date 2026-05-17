package com.codeint.shopapp.hilt.data.payment.remote

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.data.payment.*
import javax.inject.Inject

class PaymentRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val apiResponseParser: ApiResponseParser,
    private val authInterceptor: AuthInterceptor,
    private val rateLimiter: RateLimiter
) {
    fun getAll(request: PaymentRequest) = PaymentResponse(emptyList(), 0, request.page, false)
    fun getById(id: String) = PaymentEntity(id, "Payment $id")
    fun create(entity: PaymentEntity) = entity.copy(id = "new_${System.currentTimeMillis()}")
    fun update(id: String, entity: PaymentEntity) = entity
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = PaymentResponse(emptyList(), 0, page, false)
}
