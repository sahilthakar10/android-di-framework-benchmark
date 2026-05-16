package com.codeint.shopapp.metro.domain.review

import com.codeint.shopapp.metro.data.review.ReviewRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetReviewListUseCase @Inject constructor(
    private val repository: ReviewRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<ReviewDomainModel> {
        analytics.track("get_review_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetReviewDetailUseCase @Inject constructor(
    private val repository: ReviewRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): ReviewDomainModel? {
        analytics.track("get_review_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateReviewUseCase @Inject constructor(
    private val repository: ReviewRepository,
    private val logger: AppLogger
) {
    fun execute(model: ReviewDomainModel): ReviewDomainModel {
        logger.info("ReviewUseCase", "Creating review: ${model.name}")
        return repository.create(model)
    }
}

class UpdateReviewUseCase @Inject constructor(
    private val repository: ReviewRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: ReviewDomainModel): ReviewDomainModel {
        logger.info("ReviewUseCase", "Updating review: $id")
        return repository.update(id, model)
    }
}

class DeleteReviewUseCase @Inject constructor(
    private val repository: ReviewRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("ReviewUseCase", "Deleting review: $id")
        return repository.delete(id)
    }
}

class SearchReviewUseCase @Inject constructor(
    private val repository: ReviewRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<ReviewDomainModel> {
        analytics.track("search_review", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateReviewUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: ReviewDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshReviewCacheUseCase @Inject constructor(
    private val repository: ReviewRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("ReviewUseCase", "Refreshing review cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetReviewCountUseCase @Inject constructor(private val repository: ReviewRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterReviewUseCase @Inject constructor(private val repository: ReviewRepository) {
    fun execute(predicate: (ReviewDomainModel) -> Boolean): List<ReviewDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
