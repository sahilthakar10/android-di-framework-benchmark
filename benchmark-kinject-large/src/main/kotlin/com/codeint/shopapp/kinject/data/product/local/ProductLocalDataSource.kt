package com.codeint.shopapp.kinject.data.product.local

import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.data.product.*
import me.tatarka.inject.annotations.Inject

@Inject class ProductLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ProductEntity> = emptyList()
    fun getById(id: String): ProductEntity? = null
    fun save(entity: ProductEntity) { db.insert("products", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<ProductEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("products", "id = '$id'") }
    fun clear() { db.delete("products", "1=1") }
}
