package com.codeint.shopapp.koin.domain.category

import com.codeint.shopapp.koin.data.category.CategoryRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger


class GetCategoryListUseCase constructor(
    private val repository: CategoryRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<CategoryDomainModel> {
        analytics.track("get_category_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetCategoryDetailUseCase constructor(
    private val repository: CategoryRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): CategoryDomainModel? {
        analytics.track("get_category_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateCategoryUseCase constructor(
    private val repository: CategoryRepository,
    private val logger: AppLogger
) {
    fun execute(model: CategoryDomainModel): CategoryDomainModel {
        logger.info("CategoryUseCase", "Creating category: ${model.name}")
        return repository.create(model)
    }
}

class UpdateCategoryUseCase constructor(
    private val repository: CategoryRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: CategoryDomainModel): CategoryDomainModel {
        logger.info("CategoryUseCase", "Updating category: $id")
        return repository.update(id, model)
    }
}

class DeleteCategoryUseCase constructor(
    private val repository: CategoryRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("CategoryUseCase", "Deleting category: $id")
        return repository.delete(id)
    }
}

class SearchCategoryUseCase constructor(
    private val repository: CategoryRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<CategoryDomainModel> {
        analytics.track("search_category", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateCategoryUseCase constructor(private val logger: AppLogger) {
    fun execute(model: CategoryDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshCategoryCacheUseCase constructor(
    private val repository: CategoryRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("CategoryUseCase", "Refreshing category cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetCategoryCountUseCase constructor(private val repository: CategoryRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterCategoryUseCase constructor(private val repository: CategoryRepository) {
    fun execute(predicate: (CategoryDomainModel) -> Boolean): List<CategoryDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
