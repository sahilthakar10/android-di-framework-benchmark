package com.codeint.shopapp.metro.domain.shipping

import com.codeint.shopapp.metro.data.shipping.ShippingRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetShippingListUseCase @Inject constructor(
    private val repository: ShippingRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<ShippingDomainModel> {
        analytics.track("get_shipping_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetShippingDetailUseCase @Inject constructor(
    private val repository: ShippingRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): ShippingDomainModel? {
        analytics.track("get_shipping_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateShippingUseCase @Inject constructor(
    private val repository: ShippingRepository,
    private val logger: AppLogger
) {
    fun execute(model: ShippingDomainModel): ShippingDomainModel {
        logger.info("ShippingUseCase", "Creating shipping: ${model.name}")
        return repository.create(model)
    }
}

class UpdateShippingUseCase @Inject constructor(
    private val repository: ShippingRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: ShippingDomainModel): ShippingDomainModel {
        logger.info("ShippingUseCase", "Updating shipping: $id")
        return repository.update(id, model)
    }
}

class DeleteShippingUseCase @Inject constructor(
    private val repository: ShippingRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("ShippingUseCase", "Deleting shipping: $id")
        return repository.delete(id)
    }
}

class SearchShippingUseCase @Inject constructor(
    private val repository: ShippingRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<ShippingDomainModel> {
        analytics.track("search_shipping", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateShippingUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: ShippingDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshShippingCacheUseCase @Inject constructor(
    private val repository: ShippingRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("ShippingUseCase", "Refreshing shipping cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetShippingCountUseCase @Inject constructor(private val repository: ShippingRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterShippingUseCase @Inject constructor(private val repository: ShippingRepository) {
    fun execute(predicate: (ShippingDomainModel) -> Boolean): List<ShippingDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
