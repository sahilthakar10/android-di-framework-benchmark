package com.codeint.shopapp.koin.domain.address

import com.codeint.shopapp.koin.data.address.AddressRepository
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.logging.AppLogger

class GetAddressListUseCase(private val repo: AddressRepository, private val analytics: AnalyticsTracker) {
    fun execute(page: Int = 0) = repo.getAll().also { analytics.track("get_address_list") }
}
class GetAddressDetailUseCase(private val repo: AddressRepository, private val analytics: AnalyticsTracker) {
    fun execute(id: String) = repo.getById(id).also { analytics.track("get_address_detail") }
}
class CreateAddressUseCase(private val repo: AddressRepository, private val logger: AppLogger) { fun execute(m: AddressDomainModel) = repo.create(m) }
class UpdateAddressUseCase(private val repo: AddressRepository, private val logger: AppLogger) { fun execute(id: String, m: AddressDomainModel) = repo.update(id, m) }
class DeleteAddressUseCase(private val repo: AddressRepository, private val logger: AppLogger) { fun execute(id: String) = repo.delete(id) }
class SearchAddressUseCase(private val repo: AddressRepository, private val analytics: AnalyticsTracker) { fun execute(q: String, p: Int = 0) = repo.search(q, p) }
class ValidateAddressUseCase(private val logger: AppLogger) {
    fun execute(m: AddressDomainModel): ValidationResult { val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }
}
class RefreshAddressCacheUseCase(private val repo: AddressRepository, private val logger: AppLogger) { fun execute() { repo.clearCache(); repo.getAll() } }
class GetAddressCountUseCase(private val repo: AddressRepository) { fun execute() = repo.getAll().totalCount }
class FilterAddressUseCase(private val repo: AddressRepository) { fun execute(p: (AddressDomainModel) -> Boolean) = repo.getAll().items.filter(p) }

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
