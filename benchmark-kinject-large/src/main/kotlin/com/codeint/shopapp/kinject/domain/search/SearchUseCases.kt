package com.codeint.shopapp.kinject.domain.search

import com.codeint.shopapp.kinject.data.search.SearchRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetSearchListUseCase(private val repo: SearchRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_search_list") }
}
@Inject class GetSearchDetailUseCase(private val repo: SearchRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_search_detail") }
}
@Inject class CreateSearchUseCase(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(m: SearchDomainModel) = repo.create(m) }
@Inject class UpdateSearchUseCase(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(id: String, m: SearchDomainModel) = repo.update(id, m) }
@Inject class DeleteSearchUseCase(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchSearchUseCase(private val repo: SearchRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateSearchUseCase(private val logger: AppLogger) {
    fun execute(m: SearchDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshSearchCacheUseCase(private val repo: SearchRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetSearchCountUseCase(private val repo: SearchRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterSearchUseCase(private val repo: SearchRepository) { fun execute(p: (SearchDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
