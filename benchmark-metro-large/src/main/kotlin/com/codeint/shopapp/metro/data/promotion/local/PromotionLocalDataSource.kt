package com.codeint.shopapp.metro.data.promotion.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.promotion.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class PromotionLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<PromotionEntity> {
        val cached = cacheManager.get("promotion_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<PromotionEntity>()
        val fromDb = databaseManager.query("promotions")
        return fromDb.map { row -> PromotionEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): PromotionEntity? {
        val cached = cacheManager.get("promotion_$id") as? PromotionEntity
        if (cached != null) return cached
        val rows = databaseManager.query("promotions", "id = '$id'")
        return rows.firstOrNull()?.let { PromotionEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: PromotionEntity) {
        databaseManager.insert("promotions", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("promotion_${entity.id}", entity)
    }

    fun saveAll(entities: List<PromotionEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("promotion_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("promotions", "id = '$id'")
        cacheManager.evict("promotion_$id")
    }

    fun clear() {
        databaseManager.delete("promotions", "1=1")
        cacheManager.clear()
    }
}
