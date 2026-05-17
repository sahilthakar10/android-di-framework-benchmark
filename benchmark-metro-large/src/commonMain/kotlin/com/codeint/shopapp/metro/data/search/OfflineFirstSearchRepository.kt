package com.codeint.shopapp.metro.data.search

import com.codeint.shopapp.metro.data.search.remote.SearchRemoteDataSource
import com.codeint.shopapp.metro.data.search.local.SearchLocalDataSource
import com.codeint.shopapp.metro.data.search.mapper.SearchMapper
import com.codeint.shopapp.metro.domain.search.*
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirstSearchRepository @Inject constructor(
    private val remote: SearchRemoteDataSource, private val local: SearchLocalDataSource,
    private val mapper: SearchMapper, private val logger: AppLogger
) : SearchRepository {
    override fun getAll(request: SearchRequest): PagedResult<SearchDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("SearchRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: SearchDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: SearchDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
