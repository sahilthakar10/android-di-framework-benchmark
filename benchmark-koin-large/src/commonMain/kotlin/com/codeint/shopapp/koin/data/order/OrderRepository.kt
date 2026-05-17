package com.codeint.shopapp.koin.data.order

import com.codeint.shopapp.koin.domain.order.*

interface OrderRepository {
    fun getAll(request: OrderRequest = OrderRequest()): PagedResult<OrderDomainModel>
    fun getById(id: String): OrderDomainModel?
    fun create(model: OrderDomainModel): OrderDomainModel
    fun update(id: String, model: OrderDomainModel): OrderDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<OrderDomainModel>
    fun clearCache()
}
