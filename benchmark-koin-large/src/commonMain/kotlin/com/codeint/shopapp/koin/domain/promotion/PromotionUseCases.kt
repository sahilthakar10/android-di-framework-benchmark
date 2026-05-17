package com.codeint.shopapp.koin.domain.promotion

import com.codeint.shopapp.koin.data.promotion.PromotionRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetPromotionListUseCase(private val repo: PromotionRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_promotion_list") }
}
class GetPromotionDetailUseCase(private val repo: PromotionRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_promotion_detail") }
}
class CreatePromotionUseCase(private val repo: PromotionRepository, private val logger: AppLogger) { fun execute(m: PromotionDomainModel) = repo.create(m) }
class UpdatePromotionUseCase(private val repo: PromotionRepository, private val logger: AppLogger) { fun execute(id: String, m: PromotionDomainModel) = repo.update(id, m) }
class DeletePromotionUseCase(private val repo: PromotionRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchPromotionUseCase(private val repo: PromotionRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidatePromotionUseCase(private val logger: AppLogger) {
    fun execute(m: PromotionDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshPromotionCacheUseCase(private val repo: PromotionRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetPromotionCountUseCase(private val repo: PromotionRepository) { fun execute() = repo.getAll().totalCount }
class FilterPromotionUseCase(private val repo: PromotionRepository) { fun execute(p: (PromotionDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
