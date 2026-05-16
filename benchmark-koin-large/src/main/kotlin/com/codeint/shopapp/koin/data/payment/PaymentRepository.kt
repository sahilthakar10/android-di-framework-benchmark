package com.codeint.shopapp.koin.data.payment

import com.codeint.shopapp.koin.data.payment.remote.PaymentRemoteDataSource
import com.codeint.shopapp.koin.data.payment.local.PaymentLocalDataSource
import com.codeint.shopapp.koin.data.payment.mapper.PaymentMapper
import com.codeint.shopapp.koin.domain.payment.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class PaymentRepository constructor(
    private val remoteDataSource: PaymentRemoteDataSource,
    private val localDataSource: PaymentLocalDataSource,
    private val mapper: PaymentMapper,
    private val logger: AppLogger
) {
    fun getAll(request: PaymentRequest = PaymentRequest()): PagedResult<PaymentDomainModel> {
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

    fun getById(id: String): PaymentDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: PaymentDomainModel): PaymentDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: PaymentDomainModel): PaymentDomainModel {
        val entity = mapper.toEntity(model)
        val updated = remoteDataSource.update(id, entity)
        localDataSource.save(updated)
        return mapper.toDomain(updated)
    }

    fun delete(id: String): Boolean {
        val result = remoteDataSource.delete(id)
        if (result) localDataSource.delete(id)
        return result
    }

    fun search(query: String, page: Int = 0): PagedResult<PaymentDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
