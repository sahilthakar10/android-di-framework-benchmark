package com.codeint.shopapp.metro.data.category

import com.codeint.shopapp.metro.domain.category.*

interface CategoryRepository {
    fun getAll(request: CategoryRequest = CategoryRequest()): PagedResult<CategoryDomainModel>
    fun getById(id: String): CategoryDomainModel?
    fun create(model: CategoryDomainModel): CategoryDomainModel
    fun update(id: String, model: CategoryDomainModel): CategoryDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<CategoryDomainModel>
    fun clearCache()
}
