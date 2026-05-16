package com.codeint.shopapp.metro.data.payment.local

import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.data.payment.*
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class PaymentLocalDataSource @Inject constructor(
    private val databaseManager: DatabaseManager,
    private val cacheManager: CacheManager
) {
    fun getAll(): List<PaymentEntity> {
        val cached = cacheManager.get("payment_all") as? List<*>
        if (cached != null) return cached.filterIsInstance<PaymentEntity>()
        val fromDb = databaseManager.query("payments")
        return fromDb.map { row -> PaymentEntity(row["id"].toString(), row["name"].toString()) }
    }

    fun getById(id: String): PaymentEntity? {
        val cached = cacheManager.get("payment_$id") as? PaymentEntity
        if (cached != null) return cached
        val rows = databaseManager.query("payments", "id = '$id'")
        return rows.firstOrNull()?.let { PaymentEntity(it["id"].toString(), it["name"].toString()) }
    }

    fun save(entity: PaymentEntity) {
        databaseManager.insert("payments", mapOf("id" to entity.id, "name" to entity.name))
        cacheManager.put("payment_${entity.id}", entity)
    }

    fun saveAll(entities: List<PaymentEntity>) {
        databaseManager.transaction { entities.forEach { save(it) } }
        cacheManager.put("payment_all", entities)
    }

    fun delete(id: String) {
        databaseManager.delete("payments", "id = '$id'")
        cacheManager.evict("payment_$id")
    }

    fun clear() {
        databaseManager.delete("payments", "1=1")
        cacheManager.clear()
    }
}
