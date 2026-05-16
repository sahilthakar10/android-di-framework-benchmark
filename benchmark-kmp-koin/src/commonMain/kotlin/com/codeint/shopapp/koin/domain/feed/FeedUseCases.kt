package com.codeint.shopapp.koin.domain.feed

import com.codeint.shopapp.koin.data.feed.FeedRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger


class GetFeedListUseCase constructor(
    private val repository: FeedRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<FeedDomainModel> {
        analytics.track("get_feed_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetFeedDetailUseCase constructor(
    private val repository: FeedRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): FeedDomainModel? {
        analytics.track("get_feed_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateFeedUseCase constructor(
    private val repository: FeedRepository,
    private val logger: AppLogger
) {
    fun execute(model: FeedDomainModel): FeedDomainModel {
        logger.info("FeedUseCase", "Creating feed: ${model.name}")
        return repository.create(model)
    }
}

class UpdateFeedUseCase constructor(
    private val repository: FeedRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: FeedDomainModel): FeedDomainModel {
        logger.info("FeedUseCase", "Updating feed: $id")
        return repository.update(id, model)
    }
}

class DeleteFeedUseCase constructor(
    private val repository: FeedRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("FeedUseCase", "Deleting feed: $id")
        return repository.delete(id)
    }
}

class SearchFeedUseCase constructor(
    private val repository: FeedRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<FeedDomainModel> {
        analytics.track("search_feed", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateFeedUseCase constructor(private val logger: AppLogger) {
    fun execute(model: FeedDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshFeedCacheUseCase constructor(
    private val repository: FeedRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("FeedUseCase", "Refreshing feed cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetFeedCountUseCase constructor(private val repository: FeedRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterFeedUseCase constructor(private val repository: FeedRepository) {
    fun execute(predicate: (FeedDomainModel) -> Boolean): List<FeedDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
