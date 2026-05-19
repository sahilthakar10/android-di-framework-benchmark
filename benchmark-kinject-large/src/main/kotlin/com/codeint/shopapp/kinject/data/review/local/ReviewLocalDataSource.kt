package com.codeint.shopapp.kinject.data.review.local

import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.data.review.*
import me.tatarka.inject.annotations.Inject

@Inject class ReviewLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<ReviewEntity> = emptyList()
    fun getById(id: String): ReviewEntity? = null
    fun save(entity: ReviewEntity) { db.insert("reviews", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<ReviewEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("reviews", "id = '$id'") }
    fun clear() { db.delete("reviews", "1=1") }
}
