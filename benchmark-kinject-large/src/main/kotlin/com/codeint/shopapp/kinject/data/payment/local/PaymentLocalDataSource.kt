package com.codeint.shopapp.kinject.data.payment.local

import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.data.payment.*
import me.tatarka.inject.annotations.Inject

@Inject class PaymentLocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {
    fun getAll(): List<PaymentEntity> = emptyList()
    fun getById(id: String): PaymentEntity? = null
    fun save(entity: PaymentEntity) { db.insert("payments", mapOf("id" to entity.id)) }
    fun saveAll(entities: List<PaymentEntity>) { db.transaction { entities.forEach { save(it) } } }
    fun delete(id: String) { db.delete("payments", "id = '$id'") }
    fun clear() { db.delete("payments", "1=1") }
}
