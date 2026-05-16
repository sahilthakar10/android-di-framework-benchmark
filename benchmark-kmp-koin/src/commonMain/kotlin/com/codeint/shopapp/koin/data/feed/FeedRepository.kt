package com.codeint.shopapp.koin.data.feed

import com.codeint.shopapp.koin.data.feed.remote.FeedRemoteDataSource
import com.codeint.shopapp.koin.data.feed.local.FeedLocalDataSource
import com.codeint.shopapp.koin.data.feed.mapper.FeedMapper
import com.codeint.shopapp.koin.domain.feed.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class FeedRepository constructor(
    private val remoteDataSource: FeedRemoteDataSource,
    private val localDataSource: FeedLocalDataSource,
    private val mapper: FeedMapper,
    private val logger: AppLogger
) {
    fun getAll(request: FeedRequest = FeedRequest()): PagedResult<FeedDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("FeedRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    fun getById(id: String): FeedDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: FeedDomainModel): FeedDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: FeedDomainModel): FeedDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<FeedDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
