package com.codeint.shopapp.koin.data.address

import com.codeint.shopapp.koin.domain.address.*

interface AddressRepository {
    fun getAll(request: AddressRequest = AddressRequest()): PagedResult<AddressDomainModel>
    fun getById(id: String): AddressDomainModel?
    fun create(model: AddressDomainModel): AddressDomainModel
    fun update(id: String, model: AddressDomainModel): AddressDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<AddressDomainModel>
    fun clearCache()
}
