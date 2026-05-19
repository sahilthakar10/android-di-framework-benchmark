package com.codeint.shopapp.kinject.data.payment

import com.codeint.shopapp.kinject.domain.payment.*

interface PaymentRepository {
    fun getAll(request: PaymentRequest = PaymentRequest()): PagedResult<PaymentDomainModel>
    fun getById(id: String): PaymentDomainModel?
    fun create(model: PaymentDomainModel): PaymentDomainModel
    fun update(id: String, model: PaymentDomainModel): PaymentDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<PaymentDomainModel>
    fun clearCache()
}
