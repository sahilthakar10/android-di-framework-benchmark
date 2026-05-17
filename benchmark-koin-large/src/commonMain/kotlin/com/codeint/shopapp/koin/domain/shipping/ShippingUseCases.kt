package com.codeint.shopapp.koin.domain.shipping

import com.codeint.shopapp.koin.data.shipping.ShippingRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetShippingListUseCase(private val repo: ShippingRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_shipping_list") }
}
class GetShippingDetailUseCase(private val repo: ShippingRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_shipping_detail") }
}
class CreateShippingUseCase(private val repo: ShippingRepository, private val logger: AppLogger) { fun execute(m: ShippingDomainModel) = repo.create(m) }
class UpdateShippingUseCase(private val repo: ShippingRepository, private val logger: AppLogger) { fun execute(id: String, m: ShippingDomainModel) = repo.update(id, m) }
class DeleteShippingUseCase(private val repo: ShippingRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchShippingUseCase(private val repo: ShippingRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateShippingUseCase(private val logger: AppLogger) {
    fun execute(m: ShippingDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshShippingCacheUseCase(private val repo: ShippingRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetShippingCountUseCase(private val repo: ShippingRepository) { fun execute() = repo.getAll().totalCount }
class FilterShippingUseCase(private val repo: ShippingRepository) { fun execute(p: (ShippingDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
