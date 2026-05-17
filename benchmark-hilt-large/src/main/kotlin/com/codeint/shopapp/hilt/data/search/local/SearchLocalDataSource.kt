package com.codeint.shopapp.hilt.data.search.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.search.*
import javax.inject.Inject

class SearchLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<SearchEntity> = emptyList()
    fun getById(id: String): SearchEntity? = null
    fun save(entity: SearchEntity) { databaseManager.insert("searchs", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<SearchEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("searchs", "id = '$id'") }
    fun clear() { databaseManager.delete("searchs", "1=1") }
}
