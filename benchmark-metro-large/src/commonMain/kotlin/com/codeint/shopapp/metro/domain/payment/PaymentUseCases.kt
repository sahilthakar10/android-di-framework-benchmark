package com.codeint.shopapp.metro.domain.payment

import com.codeint.shopapp.metro.data.payment.PaymentRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetPaymentListUseCase @Inject constructor(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_payment_list") }
}
class GetPaymentDetailUseCase @Inject constructor(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_payment_detail") }
}
class CreatePaymentUseCase @Inject constructor(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(m: PaymentDomainModel) = repo.create(m) }
class UpdatePaymentUseCase @Inject constructor(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(id: String, m: PaymentDomainModel) = repo.update(id, m) }
class DeletePaymentUseCase @Inject constructor(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchPaymentUseCase @Inject constructor(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidatePaymentUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(m: PaymentDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshPaymentCacheUseCase @Inject constructor(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetPaymentCountUseCase @Inject constructor(private val repo: PaymentRepository) { fun execute() = repo.getAll().totalCount }
class FilterPaymentUseCase @Inject constructor(private val repo: PaymentRepository) { fun execute(p: (PaymentDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
