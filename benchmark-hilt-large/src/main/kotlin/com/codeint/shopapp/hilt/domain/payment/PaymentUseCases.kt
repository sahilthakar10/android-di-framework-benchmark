package com.codeint.shopapp.hilt.domain.payment

import com.codeint.shopapp.hilt.data.payment.PaymentRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetPaymentListUseCase @Inject constructor(
    private val repository: PaymentRepository, private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<PaymentDomainModel> {
        analytics.track("get_payment_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetPaymentDetailUseCase @Inject constructor(
    private val repository: PaymentRepository, private val analytics: AnalyticsTracker
) {
    fun execute(id: String): PaymentDomainModel? {
        analytics.track("get_payment_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreatePaymentUseCase @Inject constructor(private val repository: PaymentRepository, private val logger: AppLogger) {
    fun execute(model: PaymentDomainModel) = repository.create(model)
}

class UpdatePaymentUseCase @Inject constructor(private val repository: PaymentRepository, private val logger: AppLogger) {
    fun execute(id: String, model: PaymentDomainModel) = repository.update(id, model)
}

class DeletePaymentUseCase @Inject constructor(private val repository: PaymentRepository, private val logger: AppLogger) {
    fun execute(id: String) = repository.delete(id)
}

class SearchPaymentUseCase @Inject constructor(private val repository: PaymentRepository, private val analytics: AnalyticsTracker) {
    fun execute(query: String, page: Int = 0) = repository.search(query, page)
}

class ValidatePaymentUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: PaymentDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshPaymentCacheUseCase @Inject constructor(private val repository: PaymentRepository, private val logger: AppLogger) {
    fun execute() { repository.clearCache(); repository.getAll() }
}

class GetPaymentCountUseCase @Inject constructor(private val repository: PaymentRepository) {
    fun execute() = repository.getAll().totalCount
}

class FilterPaymentUseCase @Inject constructor(private val repository: PaymentRepository) {
    fun execute(predicate: (PaymentDomainModel) -> Boolean) = repository.getAll().items.filter(predicate)
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
