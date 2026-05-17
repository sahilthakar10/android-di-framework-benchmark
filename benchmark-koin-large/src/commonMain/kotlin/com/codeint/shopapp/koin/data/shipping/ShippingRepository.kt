package com.codeint.shopapp.koin.data.shipping

import com.codeint.shopapp.koin.domain.shipping.*

interface ShippingRepository {
    fun getAll(request: ShippingRequest = ShippingRequest()): PagedResult<ShippingDomainModel>
    fun getById(id: String): ShippingDomainModel?
    fun create(model: ShippingDomainModel): ShippingDomainModel
    fun update(id: String, model: ShippingDomainModel): ShippingDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<ShippingDomainModel>
    fun clearCache()
}
