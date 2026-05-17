package com.codeint.shopapp.hilt.data.promotion.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.promotion.*
import javax.inject.Inject

class PromotionLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<PromotionEntity> = emptyList()
    fun getById(id: String): PromotionEntity? = null
    fun save(entity: PromotionEntity) { databaseManager.insert("promotions", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<PromotionEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("promotions", "id = '$id'") }
    fun clear() { databaseManager.delete("promotions", "1=1") }
}
