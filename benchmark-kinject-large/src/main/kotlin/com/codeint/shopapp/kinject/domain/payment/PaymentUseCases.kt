package com.codeint.shopapp.kinject.domain.payment

import com.codeint.shopapp.kinject.data.payment.PaymentRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetPaymentListUseCase(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_payment_list") }
}
@Inject class GetPaymentDetailUseCase(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_payment_detail") }
}
@Inject class CreatePaymentUseCase(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(m: PaymentDomainModel) = repo.create(m) }
@Inject class UpdatePaymentUseCase(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(id: String, m: PaymentDomainModel) = repo.update(id, m) }
@Inject class DeletePaymentUseCase(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchPaymentUseCase(private val repo: PaymentRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidatePaymentUseCase(private val logger: AppLogger) {
    fun execute(m: PaymentDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshPaymentCacheUseCase(private val repo: PaymentRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetPaymentCountUseCase(private val repo: PaymentRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterPaymentUseCase(private val repo: PaymentRepository) { fun execute(p: (PaymentDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
