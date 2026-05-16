package com.codeint.shopapp.hilt.data.order

import com.codeint.shopapp.hilt.data.order.remote.OrderRemoteDataSource
import com.codeint.shopapp.hilt.data.order.local.OrderLocalDataSource
import com.codeint.shopapp.hilt.data.order.mapper.OrderMapper
import com.codeint.shopapp.hilt.domain.order.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val remoteDataSource: OrderRemoteDataSource,
    private val localDataSource: OrderLocalDataSource,
    private val mapper: OrderMapper,
    private val logger: AppLogger
) {
    fun getAll(request: OrderRequest = OrderRequest()): PagedResult<OrderDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("OrderRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    fun getById(id: String): OrderDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: OrderDomainModel): OrderDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: OrderDomainModel): OrderDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<OrderDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
