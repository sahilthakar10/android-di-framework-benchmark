package com.codeint.shopapp.koin.domain.feed

import com.codeint.shopapp.koin.data.feed.FeedRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetFeedListUseCase(private val repo: FeedRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_feed_list") }
}
class GetFeedDetailUseCase(private val repo: FeedRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_feed_detail") }
}
class CreateFeedUseCase(private val repo: FeedRepository, private val logger: AppLogger) { fun execute(m: FeedDomainModel) = repo.create(m) }
class UpdateFeedUseCase(private val repo: FeedRepository, private val logger: AppLogger) { fun execute(id: String, m: FeedDomainModel) = repo.update(id, m) }
class DeleteFeedUseCase(private val repo: FeedRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchFeedUseCase(private val repo: FeedRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateFeedUseCase(private val logger: AppLogger) {
    fun execute(m: FeedDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshFeedCacheUseCase(private val repo: FeedRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetFeedCountUseCase(private val repo: FeedRepository) { fun execute() = repo.getAll().totalCount }
class FilterFeedUseCase(private val repo: FeedRepository) { fun execute(p: (FeedDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
