package com.codeint.shopapp.hilt.data.wishlist

import com.codeint.shopapp.hilt.data.wishlist.remote.WishlistRemoteDataSource
import com.codeint.shopapp.hilt.data.wishlist.local.WishlistLocalDataSource
import com.codeint.shopapp.hilt.data.wishlist.mapper.WishlistMapper
import com.codeint.shopapp.hilt.domain.wishlist.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstWishlistRepository @Inject constructor(
    private val remoteDataSource: WishlistRemoteDataSource,
    private val localDataSource: WishlistLocalDataSource,
    private val mapper: WishlistMapper,
    private val logger: AppLogger
) : WishlistRepository {

    override fun getAll(request: WishlistRequest): PagedResult<WishlistDomainModel> {
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

    override fun getById(id: String): WishlistDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: WishlistDomainModel): WishlistDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: WishlistDomainModel): WishlistDomainModel {
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
