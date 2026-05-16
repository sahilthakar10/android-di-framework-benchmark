package com.codeint.shopapp.hilt.data.product.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.product.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<ProductEntity> {
        val cached = cacheManager.get("product_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<ProductEntity>()
        val fromDb = databaseManager.query("products")
        return fromDb.map { row -> ProductEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): ProductEntity? {
        val cached = cacheManager.get("product_$id") as? ProductEntity
        if (cached != null) return cached
        val rows = databaseManager.query("products", "id = '$id'")
        return rows.firstOrNull()?.let { ProductEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: ProductEntity) {
        databaseManager.insert("products", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("product_${entity.id}", entity)
    }

    fun saveAll(entities: List<ProductEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("product_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("products", "id = '$id'")
        cacheManager.evict("product_$id")
    }

    fun clear() {
        databaseManager.delete("products", "1=1")
        cacheManager.clear()
    }
}
