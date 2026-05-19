package com.codeint.shopapp.kinject.data.payment

data class PaymentEntity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class PaymentResponse(val items: List<PaymentEntity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class PaymentRequest(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
