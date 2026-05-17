package com.codeint.shopapp.koin.data.promotion

import com.codeint.shopapp.koin.data.promotion.remote.PromotionRemoteDataSource
import com.codeint.shopapp.koin.data.promotion.local.PromotionLocalDataSource
import com.codeint.shopapp.koin.data.promotion.mapper.PromotionMapper
import com.codeint.shopapp.koin.domain.promotion.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class OfflineFirstPromotionRepository(
    private val remote: PromotionRemoteDataSource,
    private val local: PromotionLocalDataSource,
    private val mapper: PromotionMapper,
    private val logger: AppLogger
) : PromotionRepository {
    override fun getAll(request: PromotionRequest): PagedResult<PromotionDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("PromotionRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: PromotionDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: PromotionDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
