package com.codeint.shopapp.koin.data.shipping

import com.codeint.shopapp.koin.data.shipping.remote.ShippingRemoteDataSource
import com.codeint.shopapp.koin.data.shipping.local.ShippingLocalDataSource
import com.codeint.shopapp.koin.data.shipping.mapper.ShippingMapper
import com.codeint.shopapp.koin.domain.shipping.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class OfflineFirstShippingRepository(
    private val remote: ShippingRemoteDataSource,
    private val local: ShippingLocalDataSource,
    private val mapper: ShippingMapper,
    private val logger: AppLogger
) : ShippingRepository {
    override fun getAll(request: ShippingRequest): PagedResult<ShippingDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("ShippingRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: ShippingDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: ShippingDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
