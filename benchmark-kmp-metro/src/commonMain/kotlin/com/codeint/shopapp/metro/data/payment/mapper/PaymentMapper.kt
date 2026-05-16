package com.codeint.shopapp.metro.data.payment.mapper

import com.codeint.shopapp.metro.data.payment.*
import com.codeint.shopapp.metro.domain.payment.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
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
