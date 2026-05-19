package com.codeint.shopapp.kinject.core.storage

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface DatabaseManager { fun query(table: String, where: String = ""): List<Map<String, Any>>; fun insert(table: String, values: Map<String, Any>): Long; fun update(table: String, values: Map<String, Any>, where: String): Int; fun delete(table: String, where: String): Int; fun transaction(block: () -> Unit) }
interface PreferencesManager { fun putString(k: String, v: String); fun getString(k: String): String?; fun putBoolean(k: String, v: Boolean); fun getBoolean(k: String, default: Boolean = false): Boolean; fun putLong(k: String, v: Long); fun getLong(k: String, default: Long = 0): Long; fun remove(k: String) }
interface SecureStorage { fun put(k: String, v: String); fun get(k: String): String?; fun remove(k: String) }
interface CacheManager { fun get(k: String): Any?; fun put(k: String, v: Any, ttlMs: Long = 300_000); fun evict(k: String); fun clear() }

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealDatabaseManager : DatabaseManager {
    override fun query(table: String, where: String) = emptyList<Map<String, Any>>(); override fun insert(table: String, values: Map<String, Any>) = 1L
    override fun update(table: String, values: Map<String, Any>, where: String) = 1; override fun delete(table: String, where: String) = 1; override fun transaction(block: () -> Unit) { block() }
}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealPreferencesManager : PreferencesManager {
    private val store = mutableMapOf<String, Any>()
    override fun putString(k: String, v: String) { store[k] = v }; override fun getString(k: String) = store[k] as? String
    override fun putBoolean(k: String, v: Boolean) { store[k] = v }; override fun getBoolean(k: String, default: Boolean) = store[k] as? Boolean ?: default
    override fun putLong(k: String, v: Long) { store[k] = v }; override fun getLong(k: String, default: Long) = store[k] as? Long ?: default; override fun remove(k: String) { store.remove(k) }
}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealSecureStorage : SecureStorage { private val store = mutableMapOf<String, String>(); override fun put(k: String, v: String) { store[k] = v }; override fun get(k: String) = store[k]; override fun remove(k: String) { store.remove(k) } }
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealCacheManager(private val prefs: PreferencesManager) : CacheManager {
    private val cache = mutableMapOf<String, CacheEntry>()
    override fun get(k: String): Any? = cache[k]?.takeIf { !it.isExpired() }?.value
    override fun put(k: String, v: Any, ttlMs: Long) { cache[k] = CacheEntry(v, System.currentTimeMillis() + ttlMs) }
    override fun evict(k: String) { cache.remove(k) }; override fun clear() { cache.clear() }
}
@Inject class FileManager { fun read(p: String) = ByteArray(0); fun write(p: String, d: ByteArray) {} }
@Inject class DownloadManager(private val fm: FileManager) { fun download(url: String, dest: String) = dest }
data class CacheEntry(val value: Any, val expiresAt: Long) { fun isExpired() = System.currentTimeMillis() > expiresAt }
