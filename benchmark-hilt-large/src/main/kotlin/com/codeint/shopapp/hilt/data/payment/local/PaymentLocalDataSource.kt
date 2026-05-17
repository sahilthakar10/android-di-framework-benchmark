package com.codeint.shopapp.hilt.data.payment.local

import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.data.payment.*
import javax.inject.Inject

class PaymentLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<PaymentEntity> = emptyList()
    fun getById(id: String): PaymentEntity? = null
    fun save(entity: PaymentEntity) { databaseManager.insert("payments", mapOf("id" to entity.id, "name" to entity.name)) }
    fun saveAll(entities: List<PaymentEntity>) { databaseManager.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { databaseManager.delete("payments", "id = '$id'") }
    fun clear() { databaseManager.delete("payments", "1=1") }
}
