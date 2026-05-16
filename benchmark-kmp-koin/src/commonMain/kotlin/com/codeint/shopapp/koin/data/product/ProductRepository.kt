package com.codeint.shopapp.koin.data.product

import com.codeint.shopapp.koin.data.product.remote.ProductRemoteDataSource
import com.codeint.shopapp.koin.data.product.local.ProductLocalDataSource
import com.codeint.shopapp.koin.data.product.mapper.ProductMapper
import com.codeint.shopapp.koin.domain.product.*
import com.codeint.shopapp.koin.core.logging.AppLogger

class ProductRepository constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
    private val mapper: ProductMapper,
    private val logger: AppLogger
) {
    fun getAll(request: ProductRequest = ProductRequest()): PagedResult<ProductDomainModel> {
        return try {
            val remote = remoteDataSource.getAll(request)
            localDataSource.saveAll(remote.items)
            mapper.toPagedResult(remote)
        } catch (e: Exception) {
            logger.error("ProductRepo", "Failed to fetch from remote", e)
            val local = localDataSource.getAll()
            PagedResult(mapper.toDomainList(local), local.size, 0, false)
        }
    }

    fun getById(id: String): ProductDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        remote?.let { localDataSource.save(it) }
        return remote?.let { mapper.toDomain(it) }
    }

    fun create(model: ProductDomainModel): ProductDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    fun update(id: String, model: ProductDomainModel): ProductDomainModel {
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

    fun search(query: String, page: Int = 0): PagedResult<ProductDomainModel> {
        val response = remoteDataSource.search(query, page)
        return mapper.toPagedResult(response)
    }

    fun clearCache() { localDataSource.clear() }
}
