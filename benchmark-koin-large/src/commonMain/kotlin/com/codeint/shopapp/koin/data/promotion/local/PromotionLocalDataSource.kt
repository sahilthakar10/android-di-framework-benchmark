package com.codeint.shopapp.koin.data.promotion.local

import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.data.promotion.*

class PromotionLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<PromotionEntity> = emptyList()
    fun getById(id: String): PromotionEntity? = null
    fun save(entity: PromotionEntity) { db.insert("promotions", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<PromotionEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("promotions", "id = '$id'") }
    fun clear() { db.delete("promotions", "1=1") }
}
