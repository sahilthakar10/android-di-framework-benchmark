package com.codeint.shopapp.koin.data.wishlist

import com.codeint.shopapp.koin.data.wishlist.remote.WishlistRemoteDataSource
import com.codeint.shopapp.koin.data.wishlist.local.WishlistLocalDataSource
import com.codeint.shopapp.koin.data.wishlist.mapper.WishlistMapper
import com.codeint.shopapp.koin.domain.wishlist.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class WishlistRepository constructor(
    private val remoteDataSource: WishlistRemoteDataSource,
    private val localDataSource: WishlistLocalDataSource,
    private val mapper: WishlistMapper,
    private val logger: AppLogger
) {
    fun getAll(request: WishlistRequest = WishlistRequest()): PagedResult<WishlistDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("WishlistRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    fun getById(id: String): WishlistDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: WishlistDomainModel): WishlistDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: WishlistDomainModel): WishlistDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<WishlistDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
