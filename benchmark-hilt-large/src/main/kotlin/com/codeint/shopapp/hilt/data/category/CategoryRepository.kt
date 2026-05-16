package com.codeint.shopapp.hilt.data.category

import com.codeint.shopapp.hilt.data.category.remote.CategoryRemoteDataSource
import com.codeint.shopapp.hilt.data.category.local.CategoryLocalDataSource
import com.codeint.shopapp.hilt.data.category.mapper.CategoryMapper
import com.codeint.shopapp.hilt.domain.category.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val remoteDataSource: CategoryRemoteDataSource,
    private val localDataSource: CategoryLocalDataSource,
    private val mapper: CategoryMapper,
    private val logger: AppLogger
) {
    fun getAll(request: CategoryRequest = CategoryRequest()): PagedResult<CategoryDomainModel> {
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

    fun getById(id: String): CategoryDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: CategoryDomainModel): CategoryDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: CategoryDomainModel): CategoryDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<CategoryDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
