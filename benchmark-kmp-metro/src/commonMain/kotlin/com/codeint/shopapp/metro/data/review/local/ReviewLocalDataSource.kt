package com.codeint.shopapp.metro.data.review.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.review.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class ReviewLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<ReviewEntity> {
        val cached = cacheManager.get("review_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<ReviewEntity>()
        val fromDb = databaseManager.query("reviews")
        return fromDb.map { row -> ReviewEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): ReviewEntity? {
        val cached = cacheManager.get("review_$id") as? ReviewEntity
        if (cached != null) return cached
        val rows = databaseManager.query("reviews", "id = '$id'")
        return rows.firstOrNull()?.let { ReviewEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: ReviewEntity) {
        databaseManager.insert("reviews", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("review_${entity.id}", entity)
    }

    fun saveAll(entities: List<ReviewEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("review_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("reviews", "id = '$id'")
        cacheManager.evict("review_$id")
    }

    fun clear() {
        databaseManager.delete("reviews", "1=1")
        cacheManager.clear()
    }
}
