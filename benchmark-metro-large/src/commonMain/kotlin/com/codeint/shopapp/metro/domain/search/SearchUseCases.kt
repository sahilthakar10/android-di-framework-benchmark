package com.codeint.shopapp.metro.domain.search

import com.codeint.shopapp.metro.data.search.SearchRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetSearchListUseCase @Inject constructor(private val repo: SearchRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_search_list") }
}
class GetSearchDetailUseCase @Inject constructor(private val repo: SearchRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_search_detail") }
}
class CreateSearchUseCase @Inject constructor(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(m: SearchDomainModel) = repo.create(m) }
class UpdateSearchUseCase @Inject constructor(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(id: String, m: SearchDomainModel) = repo.update(id, m) }
class DeleteSearchUseCase @Inject constructor(private val repo: SearchRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchSearchUseCase @Inject constructor(private val repo: SearchRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateSearchUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(m: SearchDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshSearchCacheUseCase @Inject constructor(private val repo: SearchRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetSearchCountUseCase @Inject constructor(private val repo: SearchRepository) { fun execute() = repo.getAll().totalCount }
class FilterSearchUseCase @Inject constructor(private val repo: SearchRepository) { fun execute(p: (SearchDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
