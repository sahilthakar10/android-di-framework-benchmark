package com.codeint.shopapp.metro.domain.product

import com.codeint.shopapp.metro.data.product.ProductRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetProductListUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<ProductDomainModel> {
        analytics.track("get_product_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetProductDetailUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): ProductDomainModel? {
        analytics.track("get_product_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateProductUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val logger: AppLogger
) {
    fun execute(model: ProductDomainModel): ProductDomainModel {
        logger.info("ProductUseCase", "Creating product: ${model.name}")
        return repository.create(model)
    }
}

class UpdateProductUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: ProductDomainModel): ProductDomainModel {
        logger.info("ProductUseCase", "Updating product: $id")
        return repository.update(id, model)
    }
}

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("ProductUseCase", "Deleting product: $id")
        return repository.delete(id)
    }
}

class SearchProductUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<ProductDomainModel> {
        analytics.track("search_product", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateProductUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: ProductDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshProductCacheUseCase @Inject constructor(
    private val repository: ProductRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("ProductUseCase", "Refreshing product cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetProductCountUseCase @Inject constructor(private val repository: ProductRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterProductUseCase @Inject constructor(private val repository: ProductRepository) {
    fun execute(predicate: (ProductDomainModel) -> Boolean): List<ProductDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
