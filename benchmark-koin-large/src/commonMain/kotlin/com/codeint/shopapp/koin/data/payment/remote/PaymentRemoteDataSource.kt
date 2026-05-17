package com.codeint.shopapp.koin.data.payment.remote

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.data.payment.*

class PaymentRemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: PaymentRequest) = PaymentResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = PaymentEntity(id, "Payment $id")
    fun create(e: PaymentEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: PaymentEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = PaymentResponse(emptyList(), 0, page, false)
}
