package com.codeint.shopapp.kinject.data.category.local

import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.data.category.*
import me.tatarka.inject.annotations.Inject

@Inject class CategoryLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<CategoryEntity> = emptyList()
    fun getById(id: String): CategoryEntity? = null
    fun save(entity: CategoryEntity) { db.insert("categorys", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<CategoryEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("categorys", "id = '$id'") }
    fun clear() { db.delete("categorys", "1=1") }
}
