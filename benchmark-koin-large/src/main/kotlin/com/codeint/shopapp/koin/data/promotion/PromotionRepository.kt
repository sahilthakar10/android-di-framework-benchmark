package com.codeint.shopapp.koin.data.promotion

import com.codeint.shopapp.koin.data.promotion.remote.PromotionRemoteDataSource
import com.codeint.shopapp.koin.data.promotion.local.PromotionLocalDataSource
import com.codeint.shopapp.koin.data.promotion.mapper.PromotionMapper
import com.codeint.shopapp.koin.domain.promotion.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class PromotionRepository constructor(
    private val remoteDataSource: PromotionRemoteDataSource,
    private val localDataSource: PromotionLocalDataSource,
    private val mapper: PromotionMapper,
    private val logger: AppLogger
) {
    fun getAll(request: PromotionRequest = PromotionRequest()): PagedResult<PromotionDomainModel> {
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

    fun getById(id: String): PromotionDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: PromotionDomainModel): PromotionDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: PromotionDomainModel): PromotionDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<PromotionDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
