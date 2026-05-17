package com.codeint.shopapp.koin.domain.product

import com.codeint.shopapp.koin.data.product.ProductRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetProductListUseCase(private val repo: ProductRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_product_list") }
}
class GetProductDetailUseCase(private val repo: ProductRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_product_detail") }
}
class CreateProductUseCase(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(m: ProductDomainModel) = repo.create(m) }
class UpdateProductUseCase(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(id: String, m: ProductDomainModel) = repo.update(id, m) }
class DeleteProductUseCase(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchProductUseCase(private val repo: ProductRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateProductUseCase(private val logger: AppLogger) {
    fun execute(m: ProductDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshProductCacheUseCase(private val repo: ProductRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetProductCountUseCase(private val repo: ProductRepository) { fun execute() = repo.getAll().totalCount }
class FilterProductUseCase(private val repo: ProductRepository) { fun execute(p: (ProductDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
