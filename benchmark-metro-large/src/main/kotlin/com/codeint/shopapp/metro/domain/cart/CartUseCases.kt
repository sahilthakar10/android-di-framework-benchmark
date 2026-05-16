package com.codeint.shopapp.metro.domain.cart

import com.codeint.shopapp.metro.data.cart.CartRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetCartListUseCase @Inject constructor(
    private val repository: CartRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<CartDomainModel> {
        analytics.track("get_cart_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetCartDetailUseCase @Inject constructor(
    private val repository: CartRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): CartDomainModel? {
        analytics.track("get_cart_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateCartUseCase @Inject constructor(
    private val repository: CartRepository,
    private val logger: AppLogger
) {
    fun execute(model: CartDomainModel): CartDomainModel {
        logger.info("CartUseCase", "Creating cart: ${model.name}")
        return repository.create(model)
    }
}

class UpdateCartUseCase @Inject constructor(
    private val repository: CartRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: CartDomainModel): CartDomainModel {
        logger.info("CartUseCase", "Updating cart: $id")
        return repository.update(id, model)
    }
}

class DeleteCartUseCase @Inject constructor(
    private val repository: CartRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("CartUseCase", "Deleting cart: $id")
        return repository.delete(id)
    }
}

class SearchCartUseCase @Inject constructor(
    private val repository: CartRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<CartDomainModel> {
        analytics.track("search_cart", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateCartUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: CartDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshCartCacheUseCase @Inject constructor(
    private val repository: CartRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("CartUseCase", "Refreshing cart cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetCartCountUseCase @Inject constructor(private val repository: CartRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterCartUseCase @Inject constructor(private val repository: CartRepository) {
    fun execute(predicate: (CartDomainModel) -> Boolean): List<CartDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
