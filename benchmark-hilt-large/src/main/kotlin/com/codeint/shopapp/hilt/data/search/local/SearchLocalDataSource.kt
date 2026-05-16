package com.codeint.shopapp.hilt.data.search.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.search.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<SearchEntity> {
        val cached = cacheManager.get("search_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<SearchEntity>()
        val fromDb = databaseManager.query("searchs")
        return fromDb.map { row -> SearchEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): SearchEntity? {
        val cached = cacheManager.get("search_$id") as? SearchEntity
        if (cached != null) return cached
        val rows = databaseManager.query("searchs", "id = '$id'")
        return rows.firstOrNull()?.let { SearchEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: SearchEntity) {
        databaseManager.insert("searchs", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("search_${entity.id}", entity)
    }

    fun saveAll(entities: List<SearchEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("search_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("searchs", "id = '$id'")
        cacheManager.evict("search_$id")
    }

    fun clear() {
        databaseManager.delete("searchs", "1=1")
        cacheManager.clear()
    }
}
