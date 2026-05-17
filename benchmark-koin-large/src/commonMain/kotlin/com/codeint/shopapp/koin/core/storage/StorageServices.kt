package com.codeint.shopapp.koin.core.storage

import com.codeint.shopapp.common.platform.currentTimeMillis
import com.codeint.shopapp.common.platform.nanoTime

interface DatabaseManager { fun query(table: String, where: String = ""): List<Map<String, Any>>; fun insert(table: String, values: Map<String, Any>): Long; fun update(table: String, values: Map<String, Any>, where: String): Int; fun delete(table: String, where: String): Int; fun transaction(block: () -> Unit) }
interface PreferencesManager { fun putString(k: String, v: String); fun getString(k: String): String?; fun putBoolean(k: String, v: Boolean); fun getBoolean(k: String, default: Boolean = false): Boolean; fun putLong(k: String, v: Long); fun getLong(k: String, default: Long = 0): Long; fun remove(k: String) }
interface SecureStorage { fun put(k: String, v: String); fun get(k: String): String?; fun remove(k: String) }
interface CacheManager { fun get(k: String): Any?; fun put(k: String, v: Any, ttlMs: Long = 300_000); fun evict(k: String); fun clear() }

class RealDatabaseManager : DatabaseManager {
    override fun query(table: String, where: String) = emptyList<Map<String, Any>>()
    override fun insert(table: String, values: Map<String, Any>) = 1L
    override fun update(table: String, values: Map<String, Any>, where: String) = 1
    override fun delete(table: String, where: String) = 1
    override fun transaction(block: () -> Unit) { block() }
}
class RealPreferencesManager : PreferencesManager {
    private val store = mutableMapOf<String, Any>()
    override fun putString(k: String, v: String) { store[k] = v }; override fun getString(k: String) = store[k] as? String
    override fun putBoolean(k: String, v: Boolean) { store[k] = v }; override fun getBoolean(k: String, default: Boolean) = store[k] as? Boolean ?: default
    override fun putLong(k: String, v: Long) { store[k] = v }; override fun getLong(k: String, default: Long) = store[k] as? Long ?: default
    override fun remove(k: String) { store.remove(k) }
}
class RealSecureStorage : SecureStorage { private val store = mutableMapOf<String, String>(); override fun put(k: String, v: String) { store[k] = v }; override fun get(k: String) = store[k]; override fun remove(k: String) { store.remove(k) } }
class RealCacheManager(private val prefs: PreferencesManager) : CacheManager {
    private val cache = mutableMapOf<String, CacheEntry>()
    override fun get(k: String): Any? = cache[k]?.takeIf { !it.isExpired() }?.value
    override fun put(k: String, v: Any, ttlMs: Long) { cache[k] = CacheEntry(v, currentTimeMillis() + ttlMs) }
    override fun evict(k: String) { cache.remove(k) }; override fun clear() { cache.clear() }
}
class FileManager { fun read(p: String) = ByteArray(0); fun write(p: String, d: ByteArray) {}; fun delete(p: String) = true }
class DownloadManager(private val fm: FileManager) { fun download(url: String, dest: String) = dest }

data class CacheEntry(val value: Any, val expiresAt: Long) { fun isExpired() = currentTimeMillis() > expiresAt }
