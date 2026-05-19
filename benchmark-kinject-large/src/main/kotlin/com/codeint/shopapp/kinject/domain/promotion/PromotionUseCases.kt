package com.codeint.shopapp.kinject.domain.promotion

import com.codeint.shopapp.kinject.data.promotion.PromotionRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetPromotionListUseCase(private val repo: PromotionRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_promotion_list") }
}
@Inject class GetPromotionDetailUseCase(private val repo: PromotionRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_promotion_detail") }
}
@Inject class CreatePromotionUseCase(private val repo: PromotionRepository, private val logger: AppLogger) { fun execute(m: PromotionDomainModel) = repo.create(m) }
@Inject class UpdatePromotionUseCase(private val repo: PromotionRepository, private val logger: AppLogger) { fun execute(id: String, m: PromotionDomainModel) = repo.update(id, m) }
@Inject class DeletePromotionUseCase(private val repo: PromotionRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchPromotionUseCase(private val repo: PromotionRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidatePromotionUseCase(private val logger: AppLogger) {
    fun execute(m: PromotionDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshPromotionCacheUseCase(private val repo: PromotionRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetPromotionCountUseCase(private val repo: PromotionRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterPromotionUseCase(private val repo: PromotionRepository) { fun execute(p: (PromotionDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
