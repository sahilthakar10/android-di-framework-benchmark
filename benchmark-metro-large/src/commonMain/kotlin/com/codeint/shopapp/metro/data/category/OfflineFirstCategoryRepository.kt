package com.codeint.shopapp.metro.data.category

import com.codeint.shopapp.metro.data.category.remote.CategoryRemoteDataSource
import com.codeint.shopapp.metro.data.category.local.CategoryLocalDataSource
import com.codeint.shopapp.metro.data.category.mapper.CategoryMapper
import com.codeint.shopapp.metro.domain.category.*
import com.codeint.shopapp.metro.core.logging.AppLogger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirstCategoryRepository @Inject constructor(
    private val remote: CategoryRemoteDataSource, private val local: CategoryLocalDataSource,
    private val mapper: CategoryMapper, private val logger: AppLogger
) : CategoryRepository {
    override fun getAll(request: CategoryRequest): PagedResult<CategoryDomainModel> {
        return try { val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }
        catch (e: Exception) { logger.error("CategoryRepo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }
    }
    override fun getById(id: String) = local.getById(id)?.let { mapper.toDomain(it) } ?: remote.getById(id).let { local.save(it); mapper.toDomain(it) }
    override fun create(model: CategoryDomainModel) = remote.create(mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun update(id: String, model: CategoryDomainModel) = remote.update(id, mapper.toEntity(model)).let { local.save(it); mapper.toDomain(it) }
    override fun delete(id: String): Boolean { val r = remote.delete(id); if (r) local.delete(id); return r }
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() { local.clear() }
}
