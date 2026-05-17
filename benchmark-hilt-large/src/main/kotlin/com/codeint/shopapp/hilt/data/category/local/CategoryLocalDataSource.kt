package com.codeint.shopapp.hilt.data.category.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.category.*
import javax.inject.Inject

class CategoryLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<CategoryEntity> = emptyList()
    fun getById(id: String): CategoryEntity? = null
    fun save(entity: CategoryEntity) { databaseManager.insert("categorys", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<CategoryEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("categorys", "id = '$id'") }
    fun clear() { databaseManager.delete("categorys", "1=1") }
}
