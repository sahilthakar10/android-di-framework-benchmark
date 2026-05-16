package com.codeint.shopapp.koin.data.user.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.user.*

class UserLocalDataSource constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<UserEntity> {
        val cached = cacheManager.get("user_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<UserEntity>()
        val fromDb = databaseManager.query("users")
        return fromDb.map { row -> UserEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): UserEntity? {
        val cached = cacheManager.get("user_$id") as? UserEntity
        if (cached != null) return cached
        val rows = databaseManager.query("users", "id = '$id'")
        return rows.firstOrNull()?.let { UserEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: UserEntity) {
        databaseManager.insert("users", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("user_${entity.id}", entity)
    }

    fun saveAll(entities: List<UserEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("user_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("users", "id = '$id'")
        cacheManager.evict("user_$id")
    }

    fun clear() {
        databaseManager.delete("users", "1=1")
        cacheManager.clear()
    }
}
