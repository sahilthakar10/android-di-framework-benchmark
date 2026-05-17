package com.codeint.shopapp.koin.domain.review

import com.codeint.shopapp.koin.data.review.ReviewRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetReviewListUseCase(private val repo: ReviewRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_review_list") }
}
class GetReviewDetailUseCase(private val repo: ReviewRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_review_detail") }
}
class CreateReviewUseCase(private val repo: ReviewRepository, private val logger: AppLogger) { fun execute(m: ReviewDomainModel) = repo.create(m) }
class UpdateReviewUseCase(private val repo: ReviewRepository, private val logger: AppLogger) { fun execute(id: String, m: ReviewDomainModel) = repo.update(id, m) }
class DeleteReviewUseCase(private val repo: ReviewRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchReviewUseCase(private val repo: ReviewRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateReviewUseCase(private val logger: AppLogger) {
    fun execute(m: ReviewDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshReviewCacheUseCase(private val repo: ReviewRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetReviewCountUseCase(private val repo: ReviewRepository) { fun execute() = repo.getAll().totalCount }
class FilterReviewUseCase(private val repo: ReviewRepository) { fun execute(p: (ReviewDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
