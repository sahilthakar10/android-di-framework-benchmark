package com.codeint.shopapp.metro.data.address.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.address.*
import dev.zacsweers.metro.Inject

class AddressLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<AddressEntity> = emptyList()
    fun getById(id: String): AddressEntity? = null
    fun save(entity: AddressEntity) { db.insert("addresss", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<AddressEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("addresss", "id = '$id'") }
    fun clear() { db.delete("addresss", "1=1") }
}
