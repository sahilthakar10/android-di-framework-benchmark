package com.codeint.shopapp.hilt.data.feed.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.feed.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<FeedEntity> {
        val cached = cacheManager.get("feed_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<FeedEntity>()
        val fromDb = databaseManager.query("feeds")
        return fromDb.map { row -> FeedEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): FeedEntity? {
        val cached = cacheManager.get("feed_$id") as? FeedEntity
        if (cached != null) return cached
        val rows = databaseManager.query("feeds", "id = '$id'")
        return rows.firstOrNull()?.let { FeedEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: FeedEntity) {
        databaseManager.insert("feeds", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("feed_${entity.id}", entity)
    }

    fun saveAll(entities: List<FeedEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("feed_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("feeds", "id = '$id'")
        cacheManager.evict("feed_$id")
    }

    fun clear() {
        databaseManager.delete("feeds", "1=1")
        cacheManager.clear()
    }
}
