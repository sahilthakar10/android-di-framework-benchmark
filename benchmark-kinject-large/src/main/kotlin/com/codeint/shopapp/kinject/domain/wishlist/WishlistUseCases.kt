package com.codeint.shopapp.kinject.domain.wishlist

import com.codeint.shopapp.kinject.data.wishlist.WishlistRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetWishlistListUseCase(private val repo: WishlistRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_wishlist_list") }
}
@Inject class GetWishlistDetailUseCase(private val repo: WishlistRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_wishlist_detail") }
}
@Inject class CreateWishlistUseCase(private val repo: WishlistRepository, private val logger: AppLogger) { fun execute(m: WishlistDomainModel) = repo.create(m) }
@Inject class UpdateWishlistUseCase(private val repo: WishlistRepository, private val logger: AppLogger) { fun execute(id: String, m: WishlistDomainModel) = repo.update(id, m) }
@Inject class DeleteWishlistUseCase(private val repo: WishlistRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchWishlistUseCase(private val repo: WishlistRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateWishlistUseCase(private val logger: AppLogger) {
    fun execute(m: WishlistDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshWishlistCacheUseCase(private val repo: WishlistRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetWishlistCountUseCase(private val repo: WishlistRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterWishlistUseCase(private val repo: WishlistRepository) { fun execute(p: (WishlistDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
