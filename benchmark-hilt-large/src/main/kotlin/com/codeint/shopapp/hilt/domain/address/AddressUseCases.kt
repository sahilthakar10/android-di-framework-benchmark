package com.codeint.shopapp.hilt.domain.address

import com.codeint.shopapp.hilt.data.address.AddressRepository
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

class GetAddressListUseCase @Inject constructor(
    private val repository: AddressRepository, private val analytics: AnalyticsTracker
) {
    fun execute(page: Int = 0, pageSize: Int = 20): PagedResult<AddressDomainModel> {
        analytics.track("get_address_list", mapOf("page" to page))
        return repository.getAll()
    }
}

class GetAddressDetailUseCase @Inject constructor(
    private val repository: AddressRepository, private val analytics: AnalyticsTracker
) {
    fun execute(id: String): AddressDomainModel? {
        analytics.track("get_address_detail", mapOf("id" to id))
        return repository.getById(id)
    }
}

class CreateAddressUseCase @Inject constructor(private val repository: AddressRepository, private val logger: AppLogger) {
    fun execute(model: AddressDomainModel) = repository.create(model)
}

class UpdateAddressUseCase @Inject constructor(private val repository: AddressRepository, private val logger: AppLogger) {
    fun execute(id: String, model: AddressDomainModel) = repository.update(id, model)
}

class DeleteAddressUseCase @Inject constructor(private val repository: AddressRepository, private val logger: AppLogger) {
    fun execute(id: String) = repository.delete(id)
}

class SearchAddressUseCase @Inject constructor(private val repository: AddressRepository, private val analytics: AnalyticsTracker) {
    fun execute(query: String, page: Int = 0) = repository.search(query, page)
}

class ValidateAddressUseCase @Inject constructor(private val logger: AppLogger) {
    fun execute(model: AddressDomainModel): ValidationResult {
        val errors = mutableListOf<String>()
        if (model.name.isBlank()) errors.add("Name is required")
        if (model.id.isBlank()) errors.add("ID is required")
        return ValidationResult(errors.isEmpty(), errors)
    }
}

class RefreshAddressCacheUseCase @Inject constructor(private val repository: AddressRepository, private val logger: AppLogger) {
    fun execute() { repository.clearCache(); repository.getAll() }
}

class GetAddressCountUseCase @Inject constructor(private val repository: AddressRepository) {
    fun execute() = repository.getAll().totalCount
}

class FilterAddressUseCase @Inject constructor(private val repository: AddressRepository) {
    fun execute(predicate: (AddressDomainModel) -> Boolean) = repository.getAll().items.filter(predicate)
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
