package com.codeint.shopapp.koin.data.search.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.search.*

class SearchLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<SearchEntity> = emptyList()
    fun getById(id: String): SearchEntity? = null
    fun save(entity: SearchEntity) { db.insert("searchs", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<SearchEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("searchs", "id = '$id'") }
    fun clear() { db.delete("searchs", "1=1") }
}
