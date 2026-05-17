package com.codeint.shopapp.metro.data.category.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.category.*
import dev.zacsweers.metro.Inject

class CategoryLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<CategoryEntity> = emptyList()
    fun getById(id: String): CategoryEntity? = null
    fun save(entity: CategoryEntity) { db.insert("categorys", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<CategoryEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("categorys", "id = '$id'") }
    fun clear() { db.delete("categorys", "1=1") }
}
