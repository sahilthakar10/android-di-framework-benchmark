package com.codeint.shopapp.hilt.domain.product

import com.codeint.shopapp.hilt.data.product.ProductRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetProductListUseCase @Inject constructor(
    private val repository: ProductRepository, private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<ProductDomainModel> {
        analytics.track("get_product_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetProductDetailUseCase @Inject constructor(
    private val repository: ProductRepository, private val analytics: AnalyticsTracker
) {
    fun execute(id: String): ProductDomainModel? {
        analytics.track("get_product_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateProductUseCase @Inject constructor(private val repository: ProductRepository, private val logger: AppLogger) {
    fun execute(model: ProductDomainModel) = repository.create(model)
}

class UpdateProductUseCase @Inject constructor(private val repository: ProductRepository, private val logger: AppLogger) {
    fun execute(id: String, model: ProductDomainModel) = repository.update(id, model)
}

class DeleteProductUseCase @Inject constructor(private val repository: ProductRepository, private val logger: AppLogger) {
    fun execute(id: String) = repository.delete(id)
}

class SearchProductUseCase @Inject constructor(private val repository: ProductRepository, private val analytics: AnalyticsTracker) {
    fun execute(query: String, page: Int = 0) = repository.search(query, page)
}

class ValidateProductUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: ProductDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshProductCacheUseCase @Inject constructor(private val repository: ProductRepository, private val logger: AppLogger) {
    fun execute() { repository.clearCache(); repository.getAll() }
}

class GetProductCountUseCase @Inject constructor(private val repository: ProductRepository) {
    fun execute() = repository.getAll().totalCount
}

class FilterProductUseCase @Inject constructor(private val repository: ProductRepository) {
    fun execute(predicate: (ProductDomainModel) -> Boolean) = repository.getAll().items.filter(predicate)
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
