package com.codeint.shopapp.kinject.data.category

import com.codeint.shopapp.kinject.data.category.remote.CategoryRemoteDataSource
import com.codeint.shopapp.kinject.data.category.local.CategoryLocalDataSource
import com.codeint.shopapp.kinject.data.category.mapper.CategoryMapper
import com.codeint.shopapp.kinject.domain.category.*
import com.codeint.shopapp.kinject.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class OfflineFirstCategoryRepository(
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
