package com.codeint.shopapp.metro.data.promotion

import com.codeint.shopapp.metro.domain.promotion.*

interface PromotionRepository {
    fun getAll(request: PromotionRequest = PromotionRequest()): PagedResult<PromotionDomainModel>
    fun getById(id: String): PromotionDomainModel?
    fun create(model: PromotionDomainModel): PromotionDomainModel
    fun update(id: String, model: PromotionDomainModel): PromotionDomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<PromotionDomainModel>
    fun clearCache()
}
