package com.codeint.shopapp.hilt.data.product.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.product.*
import javax.inject.Inject

class ProductLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<ProductEntity> = emptyList()
    fun getById(id: String): ProductEntity? = null
    fun save(entity: ProductEntity) { databaseManager.insert("products", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<ProductEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("products", "id = '$id'") }
    fun clear() { databaseManager.delete("products", "1=1") }
}
