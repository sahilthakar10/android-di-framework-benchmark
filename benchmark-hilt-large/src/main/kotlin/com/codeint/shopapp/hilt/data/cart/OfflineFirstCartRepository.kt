package com.codeint.shopapp.hilt.data.cart

import com.codeint.shopapp.hilt.data.cart.remote.CartRemoteDataSource
import com.codeint.shopapp.hilt.data.cart.local.CartLocalDataSource
import com.codeint.shopapp.hilt.data.cart.mapper.CartMapper
import com.codeint.shopapp.hilt.domain.cart.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstCartRepository @Inject constructor(
    private val remoteDataSource: CartRemoteDataSource,
    private val localDataSource: CartLocalDataSource,
    private val mapper: CartMapper,
    private val logger: AppLogger
) : CartRepository {

    override fun getAll(request: CartRequest): PagedResult<CartDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("CartRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    override fun getById(id: String): CartDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: CartDomainModel): CartDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: CartDomainModel): CartDomainModel {
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
