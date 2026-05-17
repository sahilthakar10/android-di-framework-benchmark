package com.codeint.shopapp.metro.data.search.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.search.*
import dev.zacsweers.metro.Inject

class SearchLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<SearchEntity> = emptyList()
    fun getById(id: String): SearchEntity? = null
    fun save(entity: SearchEntity) { db.insert("searchs", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<SearchEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("searchs", "id = '$id'") }
    fun clear() { db.delete("searchs", "1=1") }
}
