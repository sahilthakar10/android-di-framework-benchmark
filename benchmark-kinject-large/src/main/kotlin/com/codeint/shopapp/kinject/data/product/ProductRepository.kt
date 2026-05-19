package com.codeint.shopapp.kinject.data.product

import com.codeint.shopapp.kinject.domain.product.*

interface ProductRepository {
    fun getAll(request: ProductRequest = ProductRequest()): PagedResult<ProductDomainModel>
    fun getById(id: String): ProductDomainModel?
    fun create(model: ProductDomainModel): ProductDomainModel
    fun update(id: String, model: ProductDomainModel): ProductDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<ProductDomainModel>
    fun clearCache()
}
