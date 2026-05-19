package com.codeint.shopapp.kinject.data.feed

import com.codeint.shopapp.kinject.domain.feed.*

interface FeedRepository {
    fun getAll(request: FeedRequest = FeedRequest()): PagedResult<FeedDomainModel>
    fun getById(id: String): FeedDomainModel?
    fun create(model: FeedDomainModel): FeedDomainModel
    fun update(id: String, model: FeedDomainModel): FeedDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<FeedDomainModel>
    fun clearCache()
}
