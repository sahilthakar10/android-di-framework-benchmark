package com.codeint.shopapp.metro.data.review

import com.codeint.shopapp.metro.data.review.remote.ReviewRemoteDataSource
import com.codeint.shopapp.metro.data.review.local.ReviewLocalDataSource
import com.codeint.shopapp.metro.data.review.mapper.ReviewMapper
import com.codeint.shopapp.metro.domain.review.*
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirstReviewRepository @Inject constructor(
    private val remote: ReviewRemoteDataSource, private val local: ReviewLocalDataSource,
    private val mapper: ReviewMapper, private val logger: AppLogger
) : ReviewRepository {
    override fun getAll(request: ReviewRequest): PagedResult<ReviewDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("ReviewRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: ReviewDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: ReviewDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
