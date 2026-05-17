package com.codeint.shopapp.hilt.data.feed

import com.codeint.shopapp.hilt.data.feed.remote.FeedRemoteDataSource
import com.codeint.shopapp.hilt.data.feed.local.FeedLocalDataSource
import com.codeint.shopapp.hilt.data.feed.mapper.FeedMapper
import com.codeint.shopapp.hilt.domain.feed.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstFeedRepository @Inject constructor(
    private val remoteDataSource: FeedRemoteDataSource,
    private val localDataSource: FeedLocalDataSource,
    private val mapper: FeedMapper,
    private val logger: AppLogger
) : FeedRepository {

    override fun getAll(request: FeedRequest): PagedResult<FeedDomainModel> {
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

    override fun getById(id: String): FeedDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: FeedDomainModel): FeedDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: FeedDomainModel): FeedDomainModel {
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
