package com.codeint.shopapp.hilt.data.address.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.address.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<AddressEntity> {
        val cached = cacheManager.get("address_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<AddressEntity>()
        val fromDb = databaseManager.query("addresss")
        return fromDb.map { row -> AddressEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): AddressEntity? {
        val cached = cacheManager.get("address_$id") as? AddressEntity
        if (cached != null) return cached
        val rows = databaseManager.query("addresss", "id = '$id'")
        return rows.firstOrNull()?.let { AddressEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: AddressEntity) {
        databaseManager.insert("addresss", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("address_${entity.id}", entity)
    }

    fun saveAll(entities: List<AddressEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("address_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("addresss", "id = '$id'")
        cacheManager.evict("address_$id")
    }

    fun clear() {
        databaseManager.delete("addresss", "1=1")
        cacheManager.clear()
    }
}
