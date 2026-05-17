package com.codeint.shopapp.hilt.data.review

import com.codeint.shopapp.hilt.data.review.remote.ReviewRemoteDataSource
import com.codeint.shopapp.hilt.data.review.local.ReviewLocalDataSource
import com.codeint.shopapp.hilt.data.review.mapper.ReviewMapper
import com.codeint.shopapp.hilt.domain.review.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstReviewRepository @Inject constructor(
    private val remoteDataSource: ReviewRemoteDataSource,
    private val localDataSource: ReviewLocalDataSource,
    private val mapper: ReviewMapper,
    private val logger: AppLogger
) : ReviewRepository {

    override fun getAll(request: ReviewRequest): PagedResult<ReviewDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("ReviewRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    override fun getById(id: String): ReviewDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: ReviewDomainModel): ReviewDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: ReviewDomainModel): ReviewDomainModel {
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
