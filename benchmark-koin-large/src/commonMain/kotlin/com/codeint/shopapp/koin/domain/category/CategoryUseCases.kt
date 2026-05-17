package com.codeint.shopapp.koin.domain.category

import com.codeint.shopapp.koin.data.category.CategoryRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetCategoryListUseCase(private val repo: CategoryRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_category_list") }
}
class GetCategoryDetailUseCase(private val repo: CategoryRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_category_detail") }
}
class CreateCategoryUseCase(private val repo: CategoryRepository, private val logger: AppLogger) { fun execute(m: CategoryDomainModel) = repo.create(m) }
class UpdateCategoryUseCase(private val repo: CategoryRepository, private val logger: AppLogger) { fun execute(id: String, m: CategoryDomainModel) = repo.update(id, m) }
class DeleteCategoryUseCase(private val repo: CategoryRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchCategoryUseCase(private val repo: CategoryRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateCategoryUseCase(private val logger: AppLogger) {
    fun execute(m: CategoryDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshCategoryCacheUseCase(private val repo: CategoryRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetCategoryCountUseCase(private val repo: CategoryRepository) { fun execute() = repo.getAll().totalCount }
class FilterCategoryUseCase(private val repo: CategoryRepository) { fun execute(p: (CategoryDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
