package com.codeint.shopapp.metro.data.product.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.product.*
import dev.zacsweers.metro.Inject

class ProductLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ProductEntity> = emptyList()
    fun getById(id: String): ProductEntity? = null
    fun save(entity: ProductEntity) { db.insert("products", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<ProductEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("products", "id = '$id'") }
    fun clear() { db.delete("products", "1=1") }
}
