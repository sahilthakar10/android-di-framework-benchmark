package com.codeint.shopapp.kinject.domain.category

import com.codeint.shopapp.kinject.data.category.CategoryRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetCategoryListUseCase(private val repo: CategoryRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_category_list") }
}
@Inject class GetCategoryDetailUseCase(private val repo: CategoryRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_category_detail") }
}
@Inject class CreateCategoryUseCase(private val repo: CategoryRepository, private val logger: AppLogger) { fun execute(m: CategoryDomainModel) = repo.create(m) }
@Inject class UpdateCategoryUseCase(private val repo: CategoryRepository, private val logger: AppLogger) { fun execute(id: String, m: CategoryDomainModel) = repo.update(id, m) }
@Inject class DeleteCategoryUseCase(private val repo: CategoryRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchCategoryUseCase(private val repo: CategoryRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateCategoryUseCase(private val logger: AppLogger) {
    fun execute(m: CategoryDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshCategoryCacheUseCase(private val repo: CategoryRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetCategoryCountUseCase(private val repo: CategoryRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterCategoryUseCase(private val repo: CategoryRepository) { fun execute(p: (CategoryDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
