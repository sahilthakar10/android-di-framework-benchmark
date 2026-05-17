package com.codeint.shopapp.koin.domain.search

import com.codeint.shopapp.koin.data.search.SearchRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetSearchListUseCase(private val repo: SearchRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_search_list") }
}
class GetSearchDetailUseCase(private val repo: SearchRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_search_detail") }
}
class CreateSearchUseCase(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(m: SearchDomainModel) = repo.create(m) }
class UpdateSearchUseCase(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(id: String, m: SearchDomainModel) = repo.update(id, m) }
class DeleteSearchUseCase(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchSearchUseCase(private val repo: SearchRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateSearchUseCase(private val logger: AppLogger) {
    fun execute(m: SearchDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshSearchCacheUseCase(private val repo: SearchRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetSearchCountUseCase(private val repo: SearchRepository) { fun execute() = repo.getAll().totalCount }
class FilterSearchUseCase(private val repo: SearchRepository) { fun execute(p: (SearchDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
