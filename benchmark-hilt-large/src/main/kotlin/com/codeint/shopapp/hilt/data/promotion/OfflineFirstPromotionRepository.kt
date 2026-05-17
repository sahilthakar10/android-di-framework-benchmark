package com.codeint.shopapp.hilt.data.promotion

import com.codeint.shopapp.hilt.data.promotion.remote.PromotionRemoteDataSource
import com.codeint.shopapp.hilt.data.promotion.local.PromotionLocalDataSource
import com.codeint.shopapp.hilt.data.promotion.mapper.PromotionMapper
import com.codeint.shopapp.hilt.domain.promotion.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstPromotionRepository @Inject constructor(
    private val remoteDataSource: PromotionRemoteDataSource,
    private val localDataSource: PromotionLocalDataSource,
    private val mapper: PromotionMapper,
    private val logger: AppLogger
) : PromotionRepository {

    override fun getAll(request: PromotionRequest): PagedResult<PromotionDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("PromotionRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    override fun getById(id: String): PromotionDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: PromotionDomainModel): PromotionDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: PromotionDomainModel): PromotionDomainModel {
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
