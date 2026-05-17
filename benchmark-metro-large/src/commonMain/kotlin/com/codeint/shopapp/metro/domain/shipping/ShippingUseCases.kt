package com.codeint.shopapp.metro.domain.shipping

import com.codeint.shopapp.metro.data.shipping.ShippingRepository
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject

class GetShippingListUseCase @Inject constructor(private val repo: ShippingRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_shipping_list") }
}
class GetShippingDetailUseCase @Inject constructor(private val repo: ShippingRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_shipping_detail") }
}
class CreateShippingUseCase @Inject constructor(private val repo: ShippingRepository, private val logger: AppLogger) { fun execute(m: ShippingDomainModel) = repo.create(m) }
class UpdateShippingUseCase @Inject constructor(private val repo: ShippingRepository, private val logger: AppLogger) { fun execute(id: String, m: ShippingDomainModel) = repo.update(id, m) }
class DeleteShippingUseCase @Inject constructor(private val repo: ShippingRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchShippingUseCase @Inject constructor(private val repo: ShippingRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateShippingUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(m: ShippingDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshShippingCacheUseCase @Inject constructor(private val repo: ShippingRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetShippingCountUseCase @Inject constructor(private val repo: ShippingRepository) { fun execute() = repo.getAll().totalCount }
class FilterShippingUseCase @Inject constructor(private val repo: ShippingRepository) { fun execute(p: (ShippingDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
