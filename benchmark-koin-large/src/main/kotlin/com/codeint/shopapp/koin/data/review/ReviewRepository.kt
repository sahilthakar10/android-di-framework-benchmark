package com.codeint.shopapp.koin.data.review

import com.codeint.shopapp.koin.data.review.remote.ReviewRemoteDataSource
import com.codeint.shopapp.koin.data.review.local.ReviewLocalDataSource
import com.codeint.shopapp.koin.data.review.mapper.ReviewMapper
import com.codeint.shopapp.koin.domain.review.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class ReviewRepository constructor(
    private val remoteDataSource: ReviewRemoteDataSource,
    private val localDataSource: ReviewLocalDataSource,
    private val mapper: ReviewMapper,
    private val logger: AppLogger
) {
    fun getAll(request: ReviewRequest = ReviewRequest()): PagedResult<ReviewDomainModel> {
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

    fun getById(id: String): ReviewDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: ReviewDomainModel): ReviewDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: ReviewDomainModel): ReviewDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<ReviewDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
