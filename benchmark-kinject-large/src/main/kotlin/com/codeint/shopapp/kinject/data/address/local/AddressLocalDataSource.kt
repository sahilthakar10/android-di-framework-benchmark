package com.codeint.shopapp.kinject.data.address.local

import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.data.address.*
import me.tatarka.inject.annotations.Inject

@Inject class AddressLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<AddressEntity> = emptyList()
    fun getById(id: String): AddressEntity? = null
    fun save(entity: AddressEntity) { db.insert("addresss", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<AddressEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("addresss", "id = '$id'") }
    fun clear() { db.delete("addresss", "1=1") }
}
