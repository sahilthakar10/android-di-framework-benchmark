package com.codeint.shopapp.koin.domain.payment

import com.codeint.shopapp.koin.data.payment.PaymentRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetPaymentListUseCase(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_payment_list") }
}
class GetPaymentDetailUseCase(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_payment_detail") }
}
class CreatePaymentUseCase(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(m: PaymentDomainModel) = repo.create(m) }
class UpdatePaymentUseCase(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(id: String, m: PaymentDomainModel) = repo.update(id, m) }
class DeletePaymentUseCase(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchPaymentUseCase(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidatePaymentUseCase(private val logger: AppLogger) {
    fun execute(m: PaymentDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshPaymentCacheUseCase(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetPaymentCountUseCase(private val repo: PaymentRepository) { fun execute() = repo.getAll().totalCount }
class FilterPaymentUseCase(private val repo: PaymentRepository) { fun execute(p: (PaymentDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
