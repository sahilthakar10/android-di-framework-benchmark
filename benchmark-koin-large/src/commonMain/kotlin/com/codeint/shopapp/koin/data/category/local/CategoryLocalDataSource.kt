package com.codeint.shopapp.koin.data.category.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.category.*

class CategoryLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<CategoryEntity> = emptyList()
    fun getById(id: String): CategoryEntity? = null
    fun save(entity: CategoryEntity) { db.insert("categorys", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<CategoryEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("categorys", "id = '$id'") }
    fun clear() { db.delete("categorys", "1=1") }
}
