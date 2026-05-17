package com.codeint.shopapp.hilt.domain.cart

import com.codeint.shopapp.hilt.data.cart.CartRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetCartListUseCase @Inject constructor(
    private val repository: CartRepository, private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<CartDomainModel> {
        analytics.track("get_cart_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetCartDetailUseCase @Inject constructor(
    private val repository: CartRepository, private val analytics: AnalyticsTracker
) {
    fun execute(id: String): CartDomainModel? {
        analytics.track("get_cart_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateCartUseCase @Inject constructor(private val repository: CartRepository, private val logger: AppLogger) {
    fun execute(model: CartDomainModel) = repository.create(model)
}

class UpdateCartUseCase @Inject constructor(private val repository: CartRepository, private val logger: AppLogger) {
    fun execute(id: String, model: CartDomainModel) = repository.update(id, model)
}

class DeleteCartUseCase @Inject constructor(private val repository: CartRepository, private val logger: AppLogger) {
    fun execute(id: String) = repository.delete(id)
}

class SearchCartUseCase @Inject constructor(private val repository: CartRepository, private val analytics: AnalyticsTracker) {
    fun execute(query: String, page: Int = 0) = repository.search(query, page)
}

class ValidateCartUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: CartDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshCartCacheUseCase @Inject constructor(private val repository: CartRepository, private val logger: AppLogger) {
    fun execute() { repository.clearCache(); repository.getAll() }
}

class GetCartCountUseCase @Inject constructor(private val repository: CartRepository) {
    fun execute() = repository.getAll().totalCount
}

class FilterCartUseCase @Inject constructor(private val repository: CartRepository) {
    fun execute(predicate: (CartDomainModel) -> Boolean) = repository.getAll().items.filter(predicate)
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
