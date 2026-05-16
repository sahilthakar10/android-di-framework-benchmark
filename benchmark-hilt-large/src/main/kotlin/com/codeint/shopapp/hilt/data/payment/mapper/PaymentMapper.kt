package com.codeint.shopapp.hilt.data.payment.mapper

import com.codeint.shopapp.hilt.data.payment.*
import com.codeint.shopapp.hilt.domain.payment.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentMapper @Inject constructor() {
    fun toDomain(entity: PaymentEntity): PaymentDomainModel = PaymentDomainModel(
        id = entity.id, name = entity.name, description = entity.description,
        isActive = entity.isActive, metadata = entity.metadata
    )

    fun toDomainList(entities: List<PaymentEntity>): List<PaymentDomainModel> = entities.map { toDomain(it) }

    fun toEntity(domain: PaymentDomainModel): PaymentEntity = PaymentEntity(
        id = domain.id, name = domain.name, description = domain.description,
        isActive = domain.isActive, metadata = domain.metadata
    )

    fun toPagedResult(response: PaymentResponse): PagedResult<PaymentDomainModel> = PagedResult(
        items = toDomainList(response.items), totalCount = response.totalCount,
        page = response.page, hasMore = response.hasMore
    )
}
