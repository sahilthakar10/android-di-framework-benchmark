package com.codeint.shopapp.kinject.data.address

import com.codeint.shopapp.kinject.data.address.remote.AddressRemoteDataSource
import com.codeint.shopapp.kinject.data.address.local.AddressLocalDataSource
import com.codeint.shopapp.kinject.data.address.mapper.AddressMapper
import com.codeint.shopapp.kinject.domain.address.*
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class OfflineFirstAddressRepository(
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
