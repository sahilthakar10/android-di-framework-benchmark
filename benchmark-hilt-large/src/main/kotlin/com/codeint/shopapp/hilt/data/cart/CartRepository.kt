package com.codeint.shopapp.hilt.data.cart

import com.codeint.shopapp.hilt.data.cart.remote.CartRemoteDataSource
import com.codeint.shopapp.hilt.data.cart.local.CartLocalDataSource
import com.codeint.shopapp.hilt.data.cart.mapper.CartMapper
import com.codeint.shopapp.hilt.domain.cart.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val remoteDataSource: CartRemoteDataSource,
    private val localDataSource: CartLocalDataSource,
    private val mapper: CartMapper,
    private val logger: AppLogger
) {
    fun getAll(request: CartRequest = CartRequest()): PagedResult<CartDomainModel> {
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

    fun getById(id: String): CartDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: CartDomainModel): CartDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: CartDomainModel): CartDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<CartDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
