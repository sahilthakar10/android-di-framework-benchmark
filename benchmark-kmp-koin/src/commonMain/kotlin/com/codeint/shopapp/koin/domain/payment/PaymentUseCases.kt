package com.codeint.shopapp.koin.domain.payment

import com.codeint.shopapp.koin.data.payment.PaymentRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger


class GetPaymentListUseCase constructor(
    private val repository: PaymentRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<PaymentDomainModel> {
        analytics.track("get_payment_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetPaymentDetailUseCase constructor(
    private val repository: PaymentRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): PaymentDomainModel? {
        analytics.track("get_payment_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreatePaymentUseCase constructor(
    private val repository: PaymentRepository,
    private val logger: AppLogger
) {
    fun execute(model: PaymentDomainModel): PaymentDomainModel {
        logger.info("PaymentUseCase", "Creating payment: ${model.name}")
        return repository.create(model)
    }
}

class UpdatePaymentUseCase constructor(
    private val repository: PaymentRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: PaymentDomainModel): PaymentDomainModel {
        logger.info("PaymentUseCase", "Updating payment: $id")
        return repository.update(id, model)
    }
}

class DeletePaymentUseCase constructor(
    private val repository: PaymentRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("PaymentUseCase", "Deleting payment: $id")
        return repository.delete(id)
    }
}

class SearchPaymentUseCase constructor(
    private val repository: PaymentRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<PaymentDomainModel> {
        analytics.track("search_payment", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidatePaymentUseCase constructor(private val logger: AppLogger) {
    fun execute(model: PaymentDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshPaymentCacheUseCase constructor(
    private val repository: PaymentRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("PaymentUseCase", "Refreshing payment cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetPaymentCountUseCase constructor(private val repository: PaymentRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterPaymentUseCase constructor(private val repository: PaymentRepository) {
    fun execute(predicate: (PaymentDomainModel) -> Boolean): List<PaymentDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
