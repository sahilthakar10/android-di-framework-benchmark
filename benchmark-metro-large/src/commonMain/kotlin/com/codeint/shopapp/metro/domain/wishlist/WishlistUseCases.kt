package com.codeint.shopapp.metro.domain.wishlist

import com.codeint.shopapp.metro.data.wishlist.WishlistRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetWishlistListUseCase @Inject constructor(private val repo: WishlistRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_wishlist_list") }
}
class GetWishlistDetailUseCase @Inject constructor(private val repo: WishlistRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_wishlist_detail") }
}
class CreateWishlistUseCase @Inject constructor(private val repo: WishlistRepository, private val logger: AppLogger) { fun execute(m: WishlistDomainModel) = repo.create(m) }
class UpdateWishlistUseCase @Inject constructor(private val repo: WishlistRepository, private val logger: AppLogger) { fun execute(id: String, m: WishlistDomainModel) = repo.update(id, m) }
class DeleteWishlistUseCase @Inject constructor(private val repo: WishlistRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchWishlistUseCase @Inject constructor(private val repo: WishlistRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateWishlistUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(m: WishlistDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshWishlistCacheUseCase @Inject constructor(private val repo: WishlistRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetWishlistCountUseCase @Inject constructor(private val repo: WishlistRepository) { fun execute() = repo.getAll().totalCount }
class FilterWishlistUseCase @Inject constructor(private val repo: WishlistRepository) { fun execute(p: (WishlistDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
