package com.codeint.shopapp.metro.data.payment

import com.codeint.shopapp.metro.data.payment.remote.PaymentRemoteDataSource
import com.codeint.shopapp.metro.data.payment.local.PaymentLocalDataSource
import com.codeint.shopapp.metro.data.payment.mapper.PaymentMapper
import com.codeint.shopapp.metro.domain.payment.*
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirstPaymentRepository @Inject constructor(
    private val remote: PaymentRemoteDataSource, private val local: PaymentLocalDataSource,
    private val mapper: PaymentMapper, private val logger: AppLogger
) : PaymentRepository {
    override fun getAll(request: PaymentRequest): PagedResult<PaymentDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("PaymentRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: PaymentDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: PaymentDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
