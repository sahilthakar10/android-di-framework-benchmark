package com.codeint.shopapp.kinject.domain.product

import com.codeint.shopapp.kinject.data.product.ProductRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetProductListUseCase(private val repo: ProductRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_product_list") }
}
@Inject class GetProductDetailUseCase(private val repo: ProductRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_product_detail") }
}
@Inject class CreateProductUseCase(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(m: ProductDomainModel) = repo.create(m) }
@Inject class UpdateProductUseCase(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(id: String, m: ProductDomainModel) = repo.update(id, m) }
@Inject class DeleteProductUseCase(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchProductUseCase(private val repo: ProductRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateProductUseCase(private val logger: AppLogger) {
    fun execute(m: ProductDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshProductCacheUseCase(private val repo: ProductRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetProductCountUseCase(private val repo: ProductRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterProductUseCase(private val repo: ProductRepository) { fun execute(p: (ProductDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
