package com.codeint.shopapp.hilt.data.payment

import com.codeint.shopapp.hilt.data.payment.remote.PaymentRemoteDataSource
import com.codeint.shopapp.hilt.data.payment.local.PaymentLocalDataSource
import com.codeint.shopapp.hilt.data.payment.mapper.PaymentMapper
import com.codeint.shopapp.hilt.domain.payment.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstPaymentRepository @Inject constructor(
    private val remoteDataSource: PaymentRemoteDataSource,
    private val localDataSource: PaymentLocalDataSource,
    private val mapper: PaymentMapper,
    private val logger: AppLogger
) : PaymentRepository {

    override fun getAll(request: PaymentRequest): PagedResult<PaymentDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("PaymentRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    override fun getById(id: String): PaymentDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: PaymentDomainModel): PaymentDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: PaymentDomainModel): PaymentDomainModel {
        val entity = mapper.toEntity(model)
        val updated = remoteDataSource.update(id, entity)
        localDataSource.save(updated)
        return mapper.toDomain(updated)
    }

    override fun delete(id: String): Boolean {
        val result = remoteDataSource.delete(id)
        if (result) localDataSource.delete(id)
        return result
    }

    override fun search(query: String, page: Int) = mapper.toPagedResult(remoteDataSource.search(query, page))
    override fun clearCache() { localDataSource.clear() }
}
