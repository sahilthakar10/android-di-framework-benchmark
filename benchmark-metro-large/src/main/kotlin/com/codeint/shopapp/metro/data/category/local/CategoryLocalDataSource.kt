package com.codeint.shopapp.metro.data.category.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.category.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class CategoryLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<CategoryEntity> {
        val cached = cacheManager.get("category_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<CategoryEntity>()
        val fromDb = databaseManager.query("categorys")
        return fromDb.map { row -> CategoryEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): CategoryEntity? {
        val cached = cacheManager.get("category_$id") as? CategoryEntity
        if (cached != null) return cached
        val rows = databaseManager.query("categorys", "id = '$id'")
        return rows.firstOrNull()?.let { CategoryEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: CategoryEntity) {
        databaseManager.insert("categorys", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("category_${entity.id}", entity)
    }

    fun saveAll(entities: List<CategoryEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("category_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("categorys", "id = '$id'")
        cacheManager.evict("category_$id")
    }

    fun clear() {
        databaseManager.delete("categorys", "1=1")
        cacheManager.clear()
    }
}
