package com.codeint.shopapp.hilt.data.review

import com.codeint.shopapp.hilt.domain.review.*

interface ReviewRepository {
    fun getAll(request: ReviewRequest = ReviewRequest()): PagedResult<ReviewDomainModel>
    fun getById(id: String): ReviewDomainModel?
    fun create(model: ReviewDomainModel): ReviewDomainModel
    fun update(id: String, model: ReviewDomainModel): ReviewDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<ReviewDomainModel>
    fun clearCache()
}
