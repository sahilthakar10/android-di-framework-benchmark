package com.codeint.shopapp.kinject.domain.address

import com.codeint.shopapp.kinject.data.address.AddressRepository
import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class GetAddressListUseCase(private val repo: AddressRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_address_list") }
}
@Inject class GetAddressDetailUseCase(private val repo: AddressRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_address_detail") }
}
@Inject class CreateAddressUseCase(private val repo: AddressRepository, private val logger: AppLogger) { fun execute(m: AddressDomainModel) = repo.create(m) }
@Inject class UpdateAddressUseCase(private val repo: AddressRepository, private val logger: AppLogger) { fun execute(id: String, m: AddressDomainModel) = repo.update(id, m) }
@Inject class DeleteAddressUseCase(private val repo: AddressRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
@Inject class SearchAddressUseCase(private val repo: AddressRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
@Inject class ValidateAddressUseCase(private val logger: AppLogger) {
    fun execute(m: AddressDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
@Inject class RefreshAddressCacheUseCase(private val repo: AddressRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
@Inject class GetAddressCountUseCase(private val repo: AddressRepository) { fun execute() = repo.getAll().totalCount }
@Inject class FilterAddressUseCase(private val repo: AddressRepository) { fun execute(p: (AddressDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
