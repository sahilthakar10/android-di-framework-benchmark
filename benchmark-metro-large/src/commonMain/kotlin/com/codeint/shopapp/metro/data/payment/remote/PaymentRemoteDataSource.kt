package com.codeint.shopapp.metro.data.payment.remote

import com.codeint.shopapp.metro.benchmark.currentTimeMillis
import com.codeint.shopapp.metro.benchmark.nanoTime

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.data.payment.*
import dev.zacsweers.metro.Inject

class PaymentRemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {
    fun getAll(req: PaymentRequest) = PaymentResponse(emptyList(), 0, req.page, false)
    fun getById(id: String) = PaymentEntity(id, "Payment $id")
    fun create(e: PaymentEntity) = e.copy(id = "new_${currentTimeMillis()}")
    fun update(id: String, e: PaymentEntity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = PaymentResponse(emptyList(), 0, page, false)
}
