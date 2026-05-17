package com.codeint.shopapp.koin.domain.cart

import com.codeint.shopapp.koin.data.cart.CartRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetCartListUseCase(private val repo: CartRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_cart_list") }
}
class GetCartDetailUseCase(private val repo: CartRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_cart_detail") }
}
class CreateCartUseCase(private val repo: CartRepository, private val logger: AppLogger) { fun execute(m: CartDomainModel) = repo.create(m) }
class UpdateCartUseCase(private val repo: CartRepository, private val logger: AppLogger) { fun execute(id: String, m: CartDomainModel) = repo.update(id, m) }
class DeleteCartUseCase(private val repo: CartRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchCartUseCase(private val repo: CartRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateCartUseCase(private val logger: AppLogger) {
    fun execute(m: CartDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshCartCacheUseCase(private val repo: CartRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetCartCountUseCase(private val repo: CartRepository) { fun execute() = repo.getAll().totalCount }
class FilterCartUseCase(private val repo: CartRepository) { fun execute(p: (CartDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
