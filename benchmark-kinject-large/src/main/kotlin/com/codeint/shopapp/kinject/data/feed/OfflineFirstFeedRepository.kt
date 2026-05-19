package com.codeint.shopapp.kinject.data.feed

import com.codeint.shopapp.kinject.data.feed.remote.FeedRemoteDataSource
import com.codeint.shopapp.kinject.data.feed.local.FeedLocalDataSource
import com.codeint.shopapp.kinject.data.feed.mapper.FeedMapper
import com.codeint.shopapp.kinject.domain.feed.*
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class OfflineFirstFeedRepository(
    private val remote: FeedRemoteDataSource, private val local: FeedLocalDataSource,
    private val mapper: FeedMapper, private val logger: AppLogger
) : FeedRepository {
    override fun getAll(request: FeedRequest): PagedResult<FeedDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("FeedRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: FeedDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: FeedDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
