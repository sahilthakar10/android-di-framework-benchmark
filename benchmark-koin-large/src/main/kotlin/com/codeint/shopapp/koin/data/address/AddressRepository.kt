package com.codeint.shopapp.koin.data.address

import com.codeint.shopapp.koin.data.address.remote.AddressRemoteDataSource
import com.codeint.shopapp.koin.data.address.local.AddressLocalDataSource
import com.codeint.shopapp.koin.data.address.mapper.AddressMapper
import com.codeint.shopapp.koin.domain.address.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class AddressRepository constructor(
    private val remoteDataSource: AddressRemoteDataSource,
    private val localDataSource: AddressLocalDataSource,
    private val mapper: AddressMapper,
    private val logger: AppLogger
) {
    fun getAll(request: AddressRequest = AddressRequest()): PagedResult<AddressDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("AddressRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    fun getById(id: String): AddressDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: AddressDomainModel): AddressDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: AddressDomainModel): AddressDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<AddressDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
