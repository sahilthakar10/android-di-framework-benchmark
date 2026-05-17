package com.codeint.shopapp.hilt.data.category

import com.codeint.shopapp.hilt.data.category.remote.CategoryRemoteDataSource
import com.codeint.shopapp.hilt.data.category.local.CategoryLocalDataSource
import com.codeint.shopapp.hilt.data.category.mapper.CategoryMapper
import com.codeint.shopapp.hilt.domain.category.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstCategoryRepository @Inject constructor(
    private val remoteDataSource: CategoryRemoteDataSource,
    private val localDataSource: CategoryLocalDataSource,
    private val mapper: CategoryMapper,
    private val logger: AppLogger
) : CategoryRepository {

    override fun getAll(request: CategoryRequest): PagedResult<CategoryDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("CategoryRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    override fun getById(id: String): CategoryDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: CategoryDomainModel): CategoryDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: CategoryDomainModel): CategoryDomainModel {
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
