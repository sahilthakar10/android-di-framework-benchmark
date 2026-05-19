package com.codeint.shopapp.kinject.data.search

import com.codeint.shopapp.kinject.domain.search.*

interface SearchRepository {
    fun getAll(request: SearchRequest = SearchRequest()): PagedResult<SearchDomainModel>
    fun getById(id: String): SearchDomainModel?
    fun create(model: SearchDomainModel): SearchDomainModel
    fun update(id: String, model: SearchDomainModel): SearchDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<SearchDomainModel>
    fun clearCache()
}
