package com.codeint.shopapp.kinject.domain.feed

import com.codeint.shopapp.kinject.data.feed.FeedRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetFeedListUseCase(private val repo: FeedRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_feed_list") }
}
@Inject class GetFeedDetailUseCase(private val repo: FeedRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_feed_detail") }
}
@Inject class CreateFeedUseCase(private val repo: FeedRepository, private val logger: AppLogger) { fun execute(m: FeedDomainModel) = repo.create(m) }
@Inject class UpdateFeedUseCase(private val repo: FeedRepository, private val logger: AppLogger) { fun execute(id: String, m: FeedDomainModel) = repo.update(id, m) }
@Inject class DeleteFeedUseCase(private val repo: FeedRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchFeedUseCase(private val repo: FeedRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateFeedUseCase(private val logger: AppLogger) {
    fun execute(m: FeedDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshFeedCacheUseCase(private val repo: FeedRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetFeedCountUseCase(private val repo: FeedRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterFeedUseCase(private val repo: FeedRepository) { fun execute(p: (FeedDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
