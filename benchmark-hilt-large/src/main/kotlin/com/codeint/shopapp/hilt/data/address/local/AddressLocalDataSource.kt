package com.codeint.shopapp.hilt.data.address.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.address.*
import javax.inject.Inject

class AddressLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<AddressEntity> = emptyList()
    fun getById(id: String): AddressEntity? = null
    fun save(entity: AddressEntity) { databaseManager.insert("addresss", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<AddressEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("addresss", "id = '$id'") }
    fun clear() { databaseManager.delete("addresss", "1=1") }
}
