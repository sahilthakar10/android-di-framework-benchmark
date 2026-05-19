package com.codeint.shopapp.kinject.data.payment.mapper

import com.codeint.shopapp.kinject.data.payment.*
import com.codeint.shopapp.kinject.domain.payment.*
import me.tatarka.inject.annotations.Inject

@Inject class PaymentMapper {
    fun toDomain(e: PaymentEntity) = PaymentDomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<PaymentEntity>) = entities.map { toDomain(it) }
    fun toEntity(d: PaymentDomainModel) = PaymentEntity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: PaymentResponse) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}
