package com.codeint.shopapp.kinject.domain.cart

import com.codeint.shopapp.kinject.data.cart.CartRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetCartListUseCase(private val repo: CartRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_cart_list") }
}
@Inject class GetCartDetailUseCase(private val repo: CartRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_cart_detail") }
}
@Inject class CreateCartUseCase(private val repo: CartRepository, private val logger: AppLogger) { fun execute(m: CartDomainModel) = repo.create(m) }
@Inject class UpdateCartUseCase(private val repo: CartRepository, private val logger: AppLogger) { fun execute(id: String, m: CartDomainModel) = repo.update(id, m) }
@Inject class DeleteCartUseCase(private val repo: CartRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchCartUseCase(private val repo: CartRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateCartUseCase(private val logger: AppLogger) {
    fun execute(m: CartDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshCartCacheUseCase(private val repo: CartRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetCartCountUseCase(private val repo: CartRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterCartUseCase(private val repo: CartRepository) { fun execute(p: (CartDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
