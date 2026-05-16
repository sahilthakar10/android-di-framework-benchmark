package com.codeint.shopapp.hilt.domain.promotion

import com.codeint.shopapp.hilt.data.promotion.PromotionRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetPromotionListUseCase @Inject constructor(
    private val repository: PromotionRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<PromotionDomainModel> {
        analytics.track("get_promotion_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetPromotionDetailUseCase @Inject constructor(
    private val repository: PromotionRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): PromotionDomainModel? {
        analytics.track("get_promotion_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreatePromotionUseCase @Inject constructor(
    private val repository: PromotionRepository,
    private val logger: AppLogger
) {
    fun execute(model: PromotionDomainModel): PromotionDomainModel {
        logger.info("PromotionUseCase", "Creating promotion: ${model.name}")
        return repository.create(model)
    }
}

class UpdatePromotionUseCase @Inject constructor(
    private val repository: PromotionRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: PromotionDomainModel): PromotionDomainModel {
        logger.info("PromotionUseCase", "Updating promotion: $id")
        return repository.update(id, model)
    }
}

class DeletePromotionUseCase @Inject constructor(
    private val repository: PromotionRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("PromotionUseCase", "Deleting promotion: $id")
        return repository.delete(id)
    }
}

class SearchPromotionUseCase @Inject constructor(
    private val repository: PromotionRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<PromotionDomainModel> {
        analytics.track("search_promotion", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidatePromotionUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: PromotionDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshPromotionCacheUseCase @Inject constructor(
    private val repository: PromotionRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("PromotionUseCase", "Refreshing promotion cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetPromotionCountUseCase @Inject constructor(private val repository: PromotionRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterPromotionUseCase @Inject constructor(private val repository: PromotionRepository) {
    fun execute(predicate: (PromotionDomainModel) -> Boolean): List<PromotionDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
