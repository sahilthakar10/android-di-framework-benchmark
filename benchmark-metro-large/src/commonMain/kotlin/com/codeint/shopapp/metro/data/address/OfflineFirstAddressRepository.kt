package com.codeint.shopapp.metro.data.address

import com.codeint.shopapp.metro.data.address.remote.AddressRemoteDataSource
import com.codeint.shopapp.metro.data.address.local.AddressLocalDataSource
import com.codeint.shopapp.metro.data.address.mapper.AddressMapper
import com.codeint.shopapp.metro.domain.address.*
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirstAddressRepository @Inject constructor(
    private val remote: AddressRemoteDataSource, private val local: AddressLocalDataSource,
    private val mapper: AddressMapper, private val logger: AppLogger
) : AddressRepository {
    override fun getAll(request: AddressRequest): PagedResult<AddressDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("AddressRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: AddressDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: AddressDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
