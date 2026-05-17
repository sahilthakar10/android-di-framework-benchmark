package com.codeint.shopapp.metro.domain.product

import com.codeint.shopapp.metro.data.product.ProductRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetProductListUseCase @Inject constructor(private val repo: ProductRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_product_list") }
}
class GetProductDetailUseCase @Inject constructor(private val repo: ProductRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_product_detail") }
}
class CreateProductUseCase @Inject constructor(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(m: ProductDomainModel) = repo.create(m) }
class UpdateProductUseCase @Inject constructor(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(id: String, m: ProductDomainModel) = repo.update(id, m) }
class DeleteProductUseCase @Inject constructor(private val repo: ProductRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchProductUseCase @Inject constructor(private val repo: ProductRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateProductUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(m: ProductDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshProductCacheUseCase @Inject constructor(private val repo: ProductRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetProductCountUseCase @Inject constructor(private val repo: ProductRepository) { fun execute() = repo.getAll().totalCount }
class FilterProductUseCase @Inject constructor(private val repo: ProductRepository) { fun execute(p: (ProductDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
