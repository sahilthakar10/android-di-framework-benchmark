package com.codeint.shopapp.metro.data.cart

import com.codeint.shopapp.metro.domain.cart.*

interface CartRepository {
    fun getAll(request: CartRequest = CartRequest()): PagedResult<CartDomainModel>
    fun getById(id: String): CartDomainModel?
    fun create(model: CartDomainModel): CartDomainModel
    fun update(id: String, model: CartDomainModel): CartDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<CartDomainModel>
    fun clearCache()
}
