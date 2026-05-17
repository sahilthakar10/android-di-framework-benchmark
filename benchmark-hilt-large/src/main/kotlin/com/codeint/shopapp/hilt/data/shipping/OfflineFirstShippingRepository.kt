package com.codeint.shopapp.hilt.data.shipping

import com.codeint.shopapp.hilt.data.shipping.remote.ShippingRemoteDataSource
import com.codeint.shopapp.hilt.data.shipping.local.ShippingLocalDataSource
import com.codeint.shopapp.hilt.data.shipping.mapper.ShippingMapper
import com.codeint.shopapp.hilt.domain.shipping.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstShippingRepository @Inject constructor(
    private val remoteDataSource: ShippingRemoteDataSource,
    private val localDataSource: ShippingLocalDataSource,
    private val mapper: ShippingMapper,
    private val logger: AppLogger
) : ShippingRepository {

    override fun getAll(request: ShippingRequest): PagedResult<ShippingDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("ShippingRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    override fun getById(id: String): ShippingDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: ShippingDomainModel): ShippingDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: ShippingDomainModel): ShippingDomainModel {
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
