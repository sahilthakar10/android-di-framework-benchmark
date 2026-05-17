package com.codeint.shopapp.hilt.domain.feed

import com.codeint.shopapp.hilt.data.feed.FeedRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetFeedListUseCase @Inject constructor(
    private val repository: FeedRepository, private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<FeedDomainModel> {
        analytics.track("get_feed_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetFeedDetailUseCase @Inject constructor(
    private val repository: FeedRepository, private val analytics: AnalyticsTracker
) {
    fun execute(id: String): FeedDomainModel? {
        analytics.track("get_feed_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateFeedUseCase @Inject constructor(private val repository: FeedRepository, private val logger: AppLogger) {
    fun execute(model: FeedDomainModel) = repository.create(model)
}

class UpdateFeedUseCase @Inject constructor(private val repository: FeedRepository, private val logger: AppLogger) {
    fun execute(id: String, model: FeedDomainModel) = repository.update(id, model)
}

class DeleteFeedUseCase @Inject constructor(private val repository: FeedRepository, private val logger: AppLogger) {
    fun execute(id: String) = repository.delete(id)
}

class SearchFeedUseCase @Inject constructor(private val repository: FeedRepository, private val analytics: AnalyticsTracker) {
    fun execute(query: String, page: Int = 0) = repository.search(query, page)
}

class ValidateFeedUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: FeedDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshFeedCacheUseCase @Inject constructor(private val repository: FeedRepository, private val logger: AppLogger) {
    fun execute() { repository.clearCache(); repository.getAll() }
}

class GetFeedCountUseCase @Inject constructor(private val repository: FeedRepository) {
    fun execute() = repository.getAll().totalCount
}

class FilterFeedUseCase @Inject constructor(private val repository: FeedRepository) {
    fun execute(predicate: (FeedDomainModel) -> Boolean) = repository.getAll().items.filter(predicate)
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
