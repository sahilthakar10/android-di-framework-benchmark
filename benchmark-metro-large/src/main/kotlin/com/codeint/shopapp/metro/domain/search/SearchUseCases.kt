package com.codeint.shopapp.metro.domain.search

import com.codeint.shopapp.metro.data.search.SearchRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetSearchListUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<SearchDomainModel> {
        analytics.track("get_search_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetSearchDetailUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): SearchDomainModel? {
        analytics.track("get_search_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateSearchUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val logger: AppLogger
) {
    fun execute(model: SearchDomainModel): SearchDomainModel {
        logger.info("SearchUseCase", "Creating search: ${model.name}")
        return repository.create(model)
    }
}

class UpdateSearchUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: SearchDomainModel): SearchDomainModel {
        logger.info("SearchUseCase", "Updating search: $id")
        return repository.update(id, model)
    }
}

class DeleteSearchUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("SearchUseCase", "Deleting search: $id")
        return repository.delete(id)
    }
}

class SearchSearchUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<SearchDomainModel> {
        analytics.track("search_search", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateSearchUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: SearchDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshSearchCacheUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("SearchUseCase", "Refreshing search cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetSearchCountUseCase @Inject constructor(private val repository: SearchRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterSearchUseCase @Inject constructor(private val repository: SearchRepository) {
    fun execute(predicate: (SearchDomainModel) -> Boolean): List<SearchDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
