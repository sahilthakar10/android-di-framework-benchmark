package com.codeint.shopapp.hilt.data.product

import com.codeint.shopapp.hilt.data.product.remote.ProductRemoteDataSource
import com.codeint.shopapp.hilt.data.product.local.ProductLocalDataSource
import com.codeint.shopapp.hilt.data.product.mapper.ProductMapper
import com.codeint.shopapp.hilt.domain.product.*
import com.codeint.shopapp.hilt.core.logging.AppLogger
import javax.inject.Inject

internal class OfflineFirstProductRepository @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
    private val mapper: ProductMapper,
    private val logger: AppLogger
) : ProductRepository {

    override fun getAll(request: ProductRequest): PagedResult<ProductDomainModel> {
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

    override fun getById(id: String): ProductDomainModel? {
        val local = localDataSource.getById(id)
        if (local != null) return mapper.toDomain(local)
        val remote = remoteDataSource.getById(id)
        localDataSource.save(remote)
        return mapper.toDomain(remote)
    }

    override fun create(model: ProductDomainModel): ProductDomainModel {
        val entity = mapper.toEntity(model)
        val created = remoteDataSource.create(entity)
        localDataSource.save(created)
        return mapper.toDomain(created)
    }

    override fun update(id: String, model: ProductDomainModel): ProductDomainModel {
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
