package com.codeint.shopapp.kinject.domain.review

import com.codeint.shopapp.kinject.data.review.ReviewRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetReviewListUseCase(private val repo: ReviewRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_review_list") }
}
@Inject class GetReviewDetailUseCase(private val repo: ReviewRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_review_detail") }
}
@Inject class CreateReviewUseCase(private val repo: ReviewRepository, private val logger: AppLogger) { fun execute(m: ReviewDomainModel) = repo.create(m) }
@Inject class UpdateReviewUseCase(private val repo: ReviewRepository, private val logger: AppLogger) { fun execute(id: String, m: ReviewDomainModel) = repo.update(id, m) }
@Inject class DeleteReviewUseCase(private val repo: ReviewRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchReviewUseCase(private val repo: ReviewRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateReviewUseCase(private val logger: AppLogger) {
    fun execute(m: ReviewDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshReviewCacheUseCase(private val repo: ReviewRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetReviewCountUseCase(private val repo: ReviewRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterReviewUseCase(private val repo: ReviewRepository) { fun execute(p: (ReviewDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
