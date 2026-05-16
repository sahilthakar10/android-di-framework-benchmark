package com.codeint.shopapp.metro.domain.wishlist

import com.codeint.shopapp.metro.data.wishlist.WishlistRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetWishlistListUseCase @Inject constructor(
    private val repository: WishlistRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<WishlistDomainModel> {
        analytics.track("get_wishlist_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetWishlistDetailUseCase @Inject constructor(
    private val repository: WishlistRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(id: String): WishlistDomainModel? {
        analytics.track("get_wishlist_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateWishlistUseCase @Inject constructor(
    private val repository: WishlistRepository,
    private val logger: AppLogger
) {
    fun execute(model: WishlistDomainModel): WishlistDomainModel {
        logger.info("WishlistUseCase", "Creating wishlist: ${model.name}")
        return repository.create(model)
    }
}

class UpdateWishlistUseCase @Inject constructor(
    private val repository: WishlistRepository,
    private val logger: AppLogger
) {
    fun execute(id: String, model: WishlistDomainModel): WishlistDomainModel {
        logger.info("WishlistUseCase", "Updating wishlist: $id")
        return repository.update(id, model)
    }
}

class DeleteWishlistUseCase @Inject constructor(
    private val repository: WishlistRepository,
    private val logger: AppLogger
) {
    fun execute(id: String): Boolean {
        logger.info("WishlistUseCase", "Deleting wishlist: $id")
        return repository.delete(id)
    }
}

class SearchWishlistUseCase @Inject constructor(
    private val repository: WishlistRepository,
    private val analytics: AnalyticsTracker
) {
    fun execute(query: String, page: Int = 0): PagedResult<WishlistDomainModel> {
        analytics.track("search_wishlist", mapOf("query" to query))
        return repository.search(query, page)
    }
}

class ValidateWishlistUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: WishlistDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshWishlistCacheUseCase @Inject constructor(
    private val repository: WishlistRepository,
    private val logger: AppLogger
) {
    fun execute() {
        logger.info("WishlistUseCase", "Refreshing wishlist cache")
        repository.clearCache()
        repository.getAll()
    }
}

class GetWishlistCountUseCase @Inject constructor(private val repository: WishlistRepository) {
    fun execute(): Int = repository.getAll().totalCount
}

class FilterWishlistUseCase @Inject constructor(private val repository: WishlistRepository) {
    fun execute(predicate: (WishlistDomainModel) -> Boolean): List<WishlistDomainModel> {
        return repository.getAll().items.filter(predicate)
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
