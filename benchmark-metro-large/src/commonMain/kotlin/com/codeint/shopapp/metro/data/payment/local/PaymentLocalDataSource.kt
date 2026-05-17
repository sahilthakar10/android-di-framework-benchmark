package com.codeint.shopapp.metro.data.payment.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.payment.*
import dev.zacsweers.metro.Inject

class PaymentLocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<PaymentEntity> = emptyList()
    fun getById(id: String): PaymentEntity? = null
    fun save(entity: PaymentEntity) { db.insert("payments", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<PaymentEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("payments", "id = '$id'") }
    fun clear() { db.delete("payments", "1=1") }
}
