package com.codeint.shopapp.metro.domain.cart

import com.codeint.shopapp.metro.data.cart.CartRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetCartListUseCase @Inject constructor(private val repo: CartRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_cart_list") }
}
class GetCartDetailUseCase @Inject constructor(private val repo: CartRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_cart_detail") }
}
class CreateCartUseCase @Inject constructor(private val repo: CartRepository, private val logger: AppLogger) { fun execute(m: CartDomainModel) = repo.create(m) }
class UpdateCartUseCase @Inject constructor(private val repo: CartRepository, private val logger: AppLogger) { fun execute(id: String, m: CartDomainModel) = repo.update(id, m) }
class DeleteCartUseCase @Inject constructor(private val repo: CartRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchCartUseCase @Inject constructor(private val repo: CartRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateCartUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(m: CartDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshCartCacheUseCase @Inject constructor(private val repo: CartRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetCartCountUseCase @Inject constructor(private val repo: CartRepository) { fun execute() = repo.getAll().totalCount }
class FilterCartUseCase @Inject constructor(private val repo: CartRepository) { fun execute(p: (CartDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
