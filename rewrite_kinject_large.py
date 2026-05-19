#!/usr/bin/env python3
"""
Rewrites benchmark-kinject-large as production-quality kotlin-inject-anvil app.
Same architecture as metro-large. Uses kotlin-inject-anvil DI patterns:
  - @Inject on class (kotlin-inject style)
  - @SingleIn(AppScope::class) for singletons
  - @ContributesBinding(AppScope::class) for interface bindings
  - @MergeComponent(AppScope::class) for the component
  - @Provides in component for config objects
"""

import os
import shutil

BASE = "/Users/sahilthakar/AndroidStudioProjects/BenchMarking/benchmark-kinject-large/src/main/kotlin/com/codeint/shopapp/kinject"
PKG = "com.codeint.shopapp.kinject"

DOMAINS = ["product", "user", "cart", "order", "payment", "chat", "search",
           "review", "category", "address", "wishlist", "promotion", "shipping", "feed"]

def upper(s):
    return s[0].upper() + s[1:]

def write(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(content)

# Clean old source
if os.path.exists(BASE):
    shutil.rmtree(BASE)
os.makedirs(BASE)

# ═══════════════════════════════════════
# CORE LAYER — Interfaces + @Inject implementations
# ═══════════════════════════════════════

write(f"{BASE}/core/network/NetworkModule.kt", f"""package {PKG}.core.network

class HttpClient(val baseUrl: String, val timeout: Long)
class ApiResponseParser
class NetworkMonitor
class RetryPolicy(val maxRetries: Int, val backoffMs: Long)
class SslPinningConfig(val pins: List<String>)
""")

write(f"{BASE}/core/network/ApiServices.kt", f"""package {PKG}.core.network

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface TokenProvider {{ fun getAccessToken(): String; fun getRefreshToken(): String; fun isExpired(): Boolean }}
interface NetworkLogger {{ fun logRequest(method: String, url: String, status: Int); fun logError(url: String, error: String) }}
interface CachePolicy {{ fun isCacheable(url: String): Boolean; fun getTtl(url: String): Long }}

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealTokenProvider : TokenProvider {{
    override fun getAccessToken() = "mock_access_token"; override fun getRefreshToken() = "mock_refresh_token"; override fun isExpired() = false
}}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealNetworkLogger : NetworkLogger {{
    override fun logRequest(method: String, url: String, status: Int) {{}}; override fun logError(url: String, error: String) {{}}
}}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealCachePolicy : CachePolicy {{
    override fun isCacheable(url: String) = url.contains("/products"); override fun getTtl(url: String) = 300_000L
}}
@Inject @SingleIn(AppScope::class) class AuthInterceptor(private val tokenProvider: TokenProvider) {{ fun intercept(url: String) = mapOf("Auth" to tokenProvider.getAccessToken()) }}
@Inject @SingleIn(AppScope::class) class CacheInterceptor(private val cachePolicy: CachePolicy) {{ fun shouldCache(url: String) = cachePolicy.isCacheable(url) }}
@Inject @SingleIn(AppScope::class) class LoggingInterceptor(private val networkLogger: NetworkLogger) {{ fun log(m: String, u: String, s: Int) {{ networkLogger.logRequest(m, u, s) }} }}
@Inject @SingleIn(AppScope::class) class RateLimiter(private val config: RetryPolicy) {{ fun shouldThrottle(endpoint: String) = false }}
@Inject @SingleIn(AppScope::class) class WebSocketManager(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {{ fun connect(ch: String) {{}}; fun send(msg: String) {{}} }}
@Inject @SingleIn(AppScope::class) class GraphQLClient(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) {{ fun query(q: String) = "{{}}"; fun mutate(m: String) = "{{}}" }}
@Inject @SingleIn(AppScope::class) class FileUploader(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {{ fun upload(f: String, e: String) = "id_123" }}
""")

write(f"{BASE}/core/auth/AuthServices.kt", f"""package {PKG}.core.auth

import {PKG}.core.analytics.AnalyticsTracker
import {PKG}.core.storage.PreferencesManager
import {PKG}.core.storage.SecureStorage
import {PKG}.core.network.HttpClient
import {PKG}.core.network.TokenProvider
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface AuthManager {{ fun login(email: String, password: String): Boolean; fun logout(); fun isLoggedIn(): Boolean }}
interface TokenStorage {{ fun saveTokens(a: String, r: String); fun getAccessToken(): String?; fun hasValidToken(): Boolean; fun clear() }}
interface SessionManager {{ fun startSession(userId: String); fun getCurrentUserId(): String?; fun invalidate() }}

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealAuthManager(private val tokenStorage: TokenStorage, private val sessionManager: SessionManager, private val analytics: AnalyticsTracker) : AuthManager {{
    override fun login(email: String, password: String): Boolean {{ analytics.track("login"); return true }}
    override fun logout() {{ tokenStorage.clear(); sessionManager.invalidate() }}; override fun isLoggedIn() = tokenStorage.hasValidToken()
}}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealTokenStorage(private val secureStorage: SecureStorage) : TokenStorage {{
    override fun saveTokens(a: String, r: String) {{ secureStorage.put("token", a) }}; override fun getAccessToken() = secureStorage.get("token")
    override fun hasValidToken() = getAccessToken() != null; override fun clear() {{ secureStorage.remove("token") }}
}}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealSessionManager(private val prefs: PreferencesManager) : SessionManager {{
    override fun startSession(userId: String) {{ prefs.putString("user", userId) }}; override fun getCurrentUserId() = prefs.getString("user"); override fun invalidate() {{ prefs.remove("user") }}
}}
@Inject class BiometricAuthProvider(private val secureStorage: SecureStorage) {{ fun isAvailable() = true }}
@Inject class OAuthManager(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {{ fun authorizeWithGoogle() = true }}
@Inject class PasswordValidator {{ fun validate(password: String) = password.length >= 8 }}
@Inject class TwoFactorAuthManager(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {{ fun requestCode(phone: String) = true }}
""")

write(f"{BASE}/core/analytics/AnalyticsServices.kt", f"""package {PKG}.core.analytics

import {PKG}.core.storage.PreferencesManager
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface AnalyticsTracker {{ fun track(event: String, params: Map<String, Any> = emptyMap()); fun setUserId(userId: String); fun screen(name: String) }}
interface CrashReporter {{ fun report(t: Throwable); fun log(msg: String); fun setUserId(userId: String) }}

@Inject @SingleIn(AppScope::class) class EventBus {{ private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>(); fun post(e: AnalyticsEvent) {{ listeners.forEach {{ it(e) }} }} }}
@Inject @SingleIn(AppScope::class) class UserPropertyTracker {{ private val props = mutableMapOf<String, Any>(); fun set(k: String, v: Any) {{ props[k] = v }}; fun get(k: String) = props[k] }}
@Inject @SingleIn(AppScope::class) class RemoteConfigManager {{ fun getString(k: String, d: String) = d; fun getBoolean(k: String, d: Boolean) = d; fun getLong(k: String, d: Long) = d }}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealAnalyticsTracker(private val eventBus: EventBus, private val upt: UserPropertyTracker) : AnalyticsTracker {{
    override fun track(event: String, params: Map<String, Any>) {{ eventBus.post(AnalyticsEvent(event, params)) }}
    override fun setUserId(userId: String) {{ upt.set("user_id", userId) }}; override fun screen(name: String) {{ track("screen_view", mapOf("screen" to name)) }}
}}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealCrashReporter : CrashReporter {{ override fun report(t: Throwable) {{}}; override fun log(msg: String) {{}}; override fun setUserId(userId: String) {{}} }}
@Inject class PerformanceMonitor {{ fun startTrace(name: String) = TraceHandle(name, System.nanoTime()) }}
@Inject class ABTestManager(private val rc: RemoteConfigManager) {{ fun getVariant(id: String) = rc.getString("exp_$id", "control") }}
@Inject class ConsentManager(private val prefs: PreferencesManager) {{ fun hasAnalyticsConsent() = true }}

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
""")

write(f"{BASE}/core/storage/StorageServices.kt", f"""package {PKG}.core.storage

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface DatabaseManager {{ fun query(table: String, where: String = ""): List<Map<String, Any>>; fun insert(table: String, values: Map<String, Any>): Long; fun update(table: String, values: Map<String, Any>, where: String): Int; fun delete(table: String, where: String): Int; fun transaction(block: () -> Unit) }}
interface PreferencesManager {{ fun putString(k: String, v: String); fun getString(k: String): String?; fun putBoolean(k: String, v: Boolean); fun getBoolean(k: String, default: Boolean = false): Boolean; fun putLong(k: String, v: Long); fun getLong(k: String, default: Long = 0): Long; fun remove(k: String) }}
interface SecureStorage {{ fun put(k: String, v: String); fun get(k: String): String?; fun remove(k: String) }}
interface CacheManager {{ fun get(k: String): Any?; fun put(k: String, v: Any, ttlMs: Long = 300_000); fun evict(k: String); fun clear() }}

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealDatabaseManager : DatabaseManager {{
    override fun query(table: String, where: String) = emptyList<Map<String, Any>>(); override fun insert(table: String, values: Map<String, Any>) = 1L
    override fun update(table: String, values: Map<String, Any>, where: String) = 1; override fun delete(table: String, where: String) = 1; override fun transaction(block: () -> Unit) {{ block() }}
}}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealPreferencesManager : PreferencesManager {{
    private val store = mutableMapOf<String, Any>()
    override fun putString(k: String, v: String) {{ store[k] = v }}; override fun getString(k: String) = store[k] as? String
    override fun putBoolean(k: String, v: Boolean) {{ store[k] = v }}; override fun getBoolean(k: String, default: Boolean) = store[k] as? Boolean ?: default
    override fun putLong(k: String, v: Long) {{ store[k] = v }}; override fun getLong(k: String, default: Long) = store[k] as? Long ?: default; override fun remove(k: String) {{ store.remove(k) }}
}}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealSecureStorage : SecureStorage {{ private val store = mutableMapOf<String, String>(); override fun put(k: String, v: String) {{ store[k] = v }}; override fun get(k: String) = store[k]; override fun remove(k: String) {{ store.remove(k) }} }}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealCacheManager(private val prefs: PreferencesManager) : CacheManager {{
    private val cache = mutableMapOf<String, CacheEntry>()
    override fun get(k: String): Any? = cache[k]?.takeIf {{ !it.isExpired() }}?.value
    override fun put(k: String, v: Any, ttlMs: Long) {{ cache[k] = CacheEntry(v, System.currentTimeMillis() + ttlMs) }}
    override fun evict(k: String) {{ cache.remove(k) }}; override fun clear() {{ cache.clear() }}
}}
@Inject class FileManager {{ fun read(p: String) = ByteArray(0); fun write(p: String, d: ByteArray) {{}} }}
@Inject class DownloadManager(private val fm: FileManager) {{ fun download(url: String, dest: String) = dest }}
data class CacheEntry(val value: Any, val expiresAt: Long) {{ fun isExpired() = System.currentTimeMillis() > expiresAt }}
""")

write(f"{BASE}/core/config/ConfigServices.kt", f"""package {PKG}.core.config

import {PKG}.core.analytics.RemoteConfigManager
import {PKG}.core.storage.PreferencesManager
import me.tatarka.inject.annotations.Inject

@Inject class FeatureFlagManager(private val rc: RemoteConfigManager) {{ fun isEnabled(flag: String) = rc.getBoolean("ff_$flag", false) }}
@Inject class AppConfigProvider(private val rc: RemoteConfigManager) {{ fun getApiVersion() = rc.getString("api_version", "v3") }}
@Inject class ThemeManager(private val prefs: PreferencesManager) {{ fun isDarkMode() = prefs.getBoolean("dark_mode", false) }}
@Inject class LocaleManager(private val prefs: PreferencesManager) {{ fun getCurrentLocale() = prefs.getString("locale") ?: "en" }}
@Inject class EnvironmentManager {{ fun getEnvironment() = "production" }}
""")

write(f"{BASE}/core/logging/LoggingServices.kt", f"""package {PKG}.core.logging

import {PKG}.core.analytics.CrashReporter
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface AppLogger {{ fun debug(tag: String, msg: String); fun info(tag: String, msg: String); fun warn(tag: String, msg: String); fun error(tag: String, msg: String, t: Throwable? = null) }}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealAppLogger(private val crashReporter: CrashReporter) : AppLogger {{
    override fun debug(tag: String, msg: String) {{}}; override fun info(tag: String, msg: String) {{}}
    override fun warn(tag: String, msg: String) {{}}; override fun error(tag: String, msg: String, t: Throwable?) {{ t?.let {{ crashReporter.report(it) }} }}
}}
@Inject class AuditLogger(private val appLogger: AppLogger) {{ fun logUserAction(a: String) {{ appLogger.info("AUDIT", a) }} }}
""")

write(f"{BASE}/core/image/ImageServices.kt", f"""package {PKG}.core.image

import {PKG}.core.network.HttpClient
import {PKG}.core.storage.CacheManager
import me.tatarka.inject.annotations.Inject

@Inject class ImageLoader(private val httpClient: HttpClient, private val cacheManager: CacheManager) {{ fun load(url: String) = ByteArray(0) }}
@Inject class ImageProcessor {{ fun resize(data: ByteArray, w: Int, h: Int) = data }}
@Inject class ThumbnailGenerator(private val ip: ImageProcessor) {{ fun generate(data: ByteArray, size: Int) = ip.resize(data, size, size) }}
""")

write(f"{BASE}/core/notification/NotificationServices.kt", f"""package {PKG}.core.notification

import {PKG}.core.storage.PreferencesManager
import {PKG}.core.auth.SessionManager
import {PKG}.core.config.FeatureFlagManager
import {PKG}.core.analytics.AnalyticsTracker
import me.tatarka.inject.annotations.Inject

@Inject class NotificationManager(private val prefs: PreferencesManager) {{ fun isEnabled() = prefs.getBoolean("notif", true) }}
@Inject class PushTokenManager(private val sm: SessionManager) {{ fun getToken(): String? = null }}
@Inject class DeepLinkHandler {{ fun handle(uri: String) = DeepLinkResult(if (uri.contains("/product/")) "product" else "home", emptyMap()) }}
@Inject class InAppMessageManager(private val ff: FeatureFlagManager, private val at: AnalyticsTracker) {{ fun showBanner(msg: String, type: String) {{ at.track("banner") }} }}
data class DeepLinkResult(val destination: String, val params: Map<String, String>)
""")

write(f"{BASE}/core/location/LocationServices.kt", f"""package {PKG}.core.location

import {PKG}.core.storage.PreferencesManager
import me.tatarka.inject.annotations.Inject

@Inject class LocationManager(private val prefs: PreferencesManager) {{ fun getLastKnownLocation(): Location? = null }}
@Inject class GeocodingService {{ fun getAddress(lat: Double, lng: Double) = "123 Main St" }}
@Inject class StoreLocator(private val lm: LocationManager, private val gc: GeocodingService) {{ fun findNearbyStores() = listOf(Store("Store 1", 37.78, -122.41, 1.2)) }}
data class Location(val latitude: Double, val longitude: Double)
data class Store(val name: String, val lat: Double, val lng: Double, val distanceKm: Double)
""")

print("Core layer written")

# ═══════════════════════════════════════
# DATA LAYER
# ═══════════════════════════════════════

for domain in DOMAINS:
    cap = upper(domain)

    write(f"{BASE}/data/{domain}/Models.kt", f"""package {PKG}.data.{domain}

data class {cap}Entity(val id: String, val name: String, val description: String = "", val createdAt: Long = System.currentTimeMillis(), val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class {cap}Response(val items: List<{cap}Entity>, val totalCount: Int, val page: Int, val hasMore: Boolean)
data class {cap}Request(val query: String = "", val page: Int = 0, val pageSize: Int = 20)
""")

    write(f"{BASE}/data/{domain}/{cap}Repository.kt", f"""package {PKG}.data.{domain}

import {PKG}.domain.{domain}.*

interface {cap}Repository {{
    fun getAll(request: {cap}Request = {cap}Request()): PagedResult<{cap}DomainModel>
    fun getById(id: String): {cap}DomainModel?
    fun create(model: {cap}DomainModel): {cap}DomainModel
    fun update(id: String, model: {cap}DomainModel): {cap}DomainModel
    fun delete(id: String): Boolean
    fun search(query: String, page: Int = 0): PagedResult<{cap}DomainModel>
    fun clearCache()
}}
""")

    write(f"{BASE}/data/{domain}/remote/{cap}RemoteDataSource.kt", f"""package {PKG}.data.{domain}.remote

import {PKG}.core.network.*
import {PKG}.data.{domain}.*
import me.tatarka.inject.annotations.Inject

@Inject class {cap}RemoteDataSource(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {{
    fun getAll(req: {cap}Request) = {cap}Response(emptyList(), 0, req.page, false)
    fun getById(id: String) = {cap}Entity(id, "{cap} $id")
    fun create(e: {cap}Entity) = e.copy(id = "new_${{System.currentTimeMillis()}}")
    fun update(id: String, e: {cap}Entity) = e
    fun delete(id: String) = true
    fun search(query: String, page: Int = 0) = {cap}Response(emptyList(), 0, page, false)
}}
""")

    write(f"{BASE}/data/{domain}/local/{cap}LocalDataSource.kt", f"""package {PKG}.data.{domain}.local

import {PKG}.core.storage.*
import {PKG}.data.{domain}.*
import me.tatarka.inject.annotations.Inject

@Inject class {cap}LocalDataSource(private val db: DatabaseManager, private val cache: CacheManager) {{
    fun getAll(): List<{cap}Entity> = emptyList()
    fun getById(id: String): {cap}Entity? = null
    fun save(entity: {cap}Entity) {{ db.insert("{domain}s", mapOf("id" to entity.id)) }}
    fun saveAll(entities: List<{cap}Entity>) {{ db.transaction {{ entities.forEach {{ save(it) }} }} }}
    fun delete(id: String) {{ db.delete("{domain}s", "id = '$id'") }}
    fun clear() {{ db.delete("{domain}s", "1=1") }}
}}
""")

    write(f"{BASE}/data/{domain}/mapper/{cap}Mapper.kt", f"""package {PKG}.data.{domain}.mapper

import {PKG}.data.{domain}.*
import {PKG}.domain.{domain}.*
import me.tatarka.inject.annotations.Inject

@Inject class {cap}Mapper {{
    fun toDomain(e: {cap}Entity) = {cap}DomainModel(id = e.id, name = e.name, description = e.description, isActive = e.isActive, metadata = e.metadata)
    fun toDomainList(entities: List<{cap}Entity>) = entities.map {{ toDomain(it) }}
    fun toEntity(d: {cap}DomainModel) = {cap}Entity(id = d.id, name = d.name, description = d.description, isActive = d.isActive, metadata = d.metadata)
    fun toPagedResult(r: {cap}Response) = PagedResult(toDomainList(r.items), r.totalCount, r.page, r.hasMore)
}}
""")

    write(f"{BASE}/data/{domain}/OfflineFirst{cap}Repository.kt", f"""package {PKG}.data.{domain}

import {PKG}.data.{domain}.remote.{cap}RemoteDataSource
import {PKG}.data.{domain}.local.{cap}LocalDataSource
import {PKG}.data.{domain}.mapper.{cap}Mapper
import {PKG}.domain.{domain}.*
import {PKG}.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class OfflineFirst{cap}Repository(
    private val remote: {cap}RemoteDataSource, private val local: {cap}LocalDataSource,
    private val mapper: {cap}Mapper, private val logger: AppLogger
) : {cap}Repository {{
    override fun getAll(request: {cap}Request): PagedResult<{cap}DomainModel> {{
        return try {{ val r = remote.getAll(request); local.saveAll(r.items); mapper.toPagedResult(r) }}
        catch (e: Exception) {{ logger.error("{cap}Repo", "Failed", e); PagedResult(mapper.toDomainList(local.getAll()), 0, 0, false) }}
    }}
    override fun getById(id: String) = local.getById(id)?.let {{ mapper.toDomain(it) }} ?: remote.getById(id).let {{ local.save(it); mapper.toDomain(it) }}
    override fun create(model: {cap}DomainModel) = remote.create(mapper.toEntity(model)).let {{ local.save(it); mapper.toDomain(it) }}
    override fun update(id: String, model: {cap}DomainModel) = remote.update(id, mapper.toEntity(model)).let {{ local.save(it); mapper.toDomain(it) }}
    override fun delete(id: String): Boolean {{ val r = remote.delete(id); if (r) local.delete(id); return r }}
    override fun search(query: String, page: Int) = mapper.toPagedResult(remote.search(query, page))
    override fun clearCache() {{ local.clear() }}
}}
""")

print("Data layer written")

# ═══════════════════════════════════════
# DOMAIN LAYER — Same, with @Inject
# ═══════════════════════════════════════

for domain in DOMAINS:
    cap = upper(domain)

    write(f"{BASE}/domain/{domain}/{cap}DomainModel.kt", f"""package {PKG}.domain.{domain}

data class {cap}DomainModel(val id: String, val name: String, val description: String = "", val isActive: Boolean = true, val metadata: Map<String, String> = emptyMap())
data class PagedResult<T>(val items: List<T>, val totalCount: Int, val page: Int, val hasMore: Boolean)
""")

    write(f"{BASE}/domain/{domain}/{cap}UseCases.kt", f"""package {PKG}.domain.{domain}

import {PKG}.data.{domain}.{cap}Repository
import {PKG}.core.analytics.AnalyticsTracker
import {PKG}.core.logging.AppLogger
import me.tatarka.inject.annotations.Inject

@Inject class Get{cap}ListUseCase(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{
    fun execute(page: Int = 0) = repo.getAll().also {{ analytics.track("get_{domain}_list") }}
}}
@Inject class Get{cap}DetailUseCase(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{
    fun execute(id: String) = repo.getById(id).also {{ analytics.track("get_{domain}_detail") }}
}}
@Inject class Create{cap}UseCase(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(m: {cap}DomainModel) = repo.create(m) }}
@Inject class Update{cap}UseCase(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(id: String, m: {cap}DomainModel) = repo.update(id, m) }}
@Inject class Delete{cap}UseCase(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(id: String) = repo.delete(id) }}
@Inject class Search{cap}UseCase(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{ fun execute(q: String, p: Int = 0) = repo.search(q, p) }}
@Inject class Validate{cap}UseCase(private val logger: AppLogger) {{
    fun execute(m: {cap}DomainModel): ValidationResult {{ val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }}
}}
@Inject class Refresh{cap}CacheUseCase(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute() {{ repo.clearCache(); repo.getAll() }} }}
@Inject class Get{cap}CountUseCase(private val repo: {cap}Repository) {{ fun execute() = repo.getAll().totalCount }}
@Inject class Filter{cap}UseCase(private val repo: {cap}Repository) {{ fun execute(p: ({cap}DomainModel) -> Boolean) = repo.getAll().items.filter(p) }}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
""")

print("Domain layer written")

# ═══════════════════════════════════════
# FEATURE LAYER — ViewModels with @Inject
# ═══════════════════════════════════════

vms = [
    ("home", "HomeViewModel", "GetProductListUseCase,GetCategoryListUseCase,GetPromotionListUseCase,GetFeedListUseCase,FeatureFlagManager", "product,category,promotion,feed"),
    ("search", "SearchViewModel", "SearchProductUseCase", "product"),
    ("productdetail", "ProductDetailViewModel", "GetProductDetailUseCase,GetReviewListUseCase", "product,review"),
    ("cart", "CartViewModel", "GetCartListUseCase,DeleteCartUseCase", "cart"),
    ("checkout", "CheckoutViewModel", "CreateOrderUseCase,GetAddressListUseCase,GetPaymentListUseCase", "order,address,payment"),
    ("profile", "ProfileViewModel", "GetUserDetailUseCase,GetOrderListUseCase", "user,order"),
    ("chat", "ChatViewModel", "GetChatListUseCase", "chat"),
    ("orders", "OrderHistoryViewModel", "GetOrderListUseCase", "order"),
    ("settings", "SettingsViewModel", "ThemeManager,LocaleManager", ""),
    ("notifications", "NotificationsViewModel", "DeepLinkHandler", ""),
    ("onboarding", "OnboardingViewModel", "AuthManager", ""),
    ("reviews", "ReviewsViewModel", "GetReviewListUseCase", "review"),
    ("wishlist", "WishlistViewModel", "GetWishlistListUseCase", "wishlist"),
]

for feature, vm_name, deps_str, domains_str in vms:
    imports = f"import androidx.lifecycle.ViewModel\nimport androidx.lifecycle.viewModelScope\nimport {PKG}.core.analytics.AnalyticsTracker\nimport me.tatarka.inject.annotations.Inject\n"
    imports += "import kotlinx.coroutines.flow.MutableStateFlow\nimport kotlinx.coroutines.flow.StateFlow\nimport kotlinx.coroutines.flow.asStateFlow\nimport kotlinx.coroutines.launch\n"
    for d in domains_str.split(","):
        if d: imports += f"import {PKG}.domain.{d}.*\n"
    for dep in deps_str.split(","):
        dep = dep.strip()
        if dep == "FeatureFlagManager": imports += f"import {PKG}.core.config.FeatureFlagManager\n"
        elif dep == "ThemeManager": imports += f"import {PKG}.core.config.ThemeManager\n"
        elif dep == "LocaleManager": imports += f"import {PKG}.core.config.LocaleManager\n"
        elif dep == "DeepLinkHandler": imports += f"import {PKG}.core.notification.DeepLinkHandler\n"
        elif dep == "AuthManager": imports += f"import {PKG}.core.auth.AuthManager\n"

    params = []
    for dep in deps_str.split(","):
        dep = dep.strip()
        if not dep: continue
        param_name = dep[0].lower() + dep[1:]
        params.append(f"    private val {param_name}: {dep},")
    params.append("    private val analytics: AnalyticsTracker")

    write(f"{BASE}/feature/{feature}/{vm_name}.kt", f"""package {PKG}.feature.{feature}

{imports}
@Inject class {vm_name}(
{chr(10).join(params)}
) : ViewModel() {{

    private val _uiState = MutableStateFlow<{vm_name}UiState>({vm_name}UiState.Loading)
    val uiState: StateFlow<{vm_name}UiState> = _uiState.asStateFlow()

    init {{ load() }}

    fun load() {{
        viewModelScope.launch {{
            _uiState.value = {vm_name}UiState.Loading
            try {{
                analytics.screen("{feature}")
                _uiState.value = {vm_name}UiState.Success
            }} catch (e: Exception) {{
                _uiState.value = {vm_name}UiState.Error(e.message ?: "Unknown error")
            }}
        }}
    }}
}}

sealed interface {vm_name}UiState {{
    data object Loading : {vm_name}UiState
    data object Success : {vm_name}UiState
    data class Error(val message: String) : {vm_name}UiState
}}
""")

print("Feature layer written")

# ═══════════════════════════════════════
# COMPONENT — @MergeComponent with @Provides for config objects
# ═══════════════════════════════════════

component_imports = f"""package {PKG}.component

import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import {PKG}.core.network.*
import {PKG}.core.auth.*
import {PKG}.core.analytics.*
import {PKG}.core.storage.*
import {PKG}.core.config.*
import {PKG}.core.logging.*
import {PKG}.core.image.*
import {PKG}.core.notification.*
import {PKG}.core.location.*
"""

for d in DOMAINS:
    c = upper(d)
    component_imports += f"import {PKG}.data.{d}.{c}Repository\nimport {PKG}.data.{d}.OfflineFirst{c}Repository\n"
    component_imports += f"import {PKG}.data.{d}.remote.{c}RemoteDataSource\nimport {PKG}.data.{d}.local.{c}LocalDataSource\nimport {PKG}.data.{d}.mapper.{c}Mapper\n"
    component_imports += f"import {PKG}.domain.{d}.*\n"

component_imports += f"""import {PKG}.feature.home.HomeViewModel
import {PKG}.feature.search.SearchViewModel
import {PKG}.feature.productdetail.ProductDetailViewModel
import {PKG}.feature.cart.CartViewModel
import {PKG}.feature.checkout.CheckoutViewModel
import {PKG}.feature.profile.ProfileViewModel
import {PKG}.feature.chat.ChatViewModel
import {PKG}.feature.orders.OrderHistoryViewModel
import {PKG}.feature.settings.SettingsViewModel
import {PKG}.feature.notifications.NotificationsViewModel
import {PKG}.feature.onboarding.OnboardingViewModel
import {PKG}.feature.reviews.ReviewsViewModel
import {PKG}.feature.wishlist.WishlistViewModel
"""

component_body = """
@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class ShopAppComponent {

    // Config objects via @Provides
    @Provides fun provideHttpClient(): HttpClient = HttpClient("https://api.shopapp.com", 30_000)
    @Provides fun provideApiResponseParser(): ApiResponseParser = ApiResponseParser()
    @Provides fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor()
    @Provides fun provideRetryPolicy(): RetryPolicy = RetryPolicy(3, 1000)
    @Provides fun provideSslPinningConfig(): SslPinningConfig = SslPinningConfig(listOf("sha256/abc123"))

    // ── Singleton Accessors ──
    abstract val httpClient: HttpClient
    abstract val apiResponseParser: ApiResponseParser
    abstract val networkMonitor: NetworkMonitor
    abstract val retryPolicy: RetryPolicy
    abstract val sslPinningConfig: SslPinningConfig
    abstract val tokenProvider: TokenProvider
    abstract val networkLogger: NetworkLogger
    abstract val cachePolicy: CachePolicy
    abstract val authManager: AuthManager
    abstract val tokenStorage: TokenStorage
    abstract val sessionManager: SessionManager
    abstract val analyticsTracker: AnalyticsTracker
    abstract val crashReporter: CrashReporter
    abstract val databaseManager: DatabaseManager
    abstract val preferencesManager: PreferencesManager
    abstract val secureStorage: SecureStorage
    abstract val cacheManager: CacheManager
    abstract val appLogger: AppLogger

    // ── ViewModel Accessors ──
"""

# ViewModels
for _, vm_name, _, _ in vms:
    prop = vm_name[0].lower() + vm_name[1:]
    component_body += f"    abstract val {prop}: {vm_name}\n"

# Core services
component_body += """
    abstract val authInterceptor: AuthInterceptor
    abstract val rateLimiter: RateLimiter
    abstract val webSocketManager: WebSocketManager
    abstract val graphQLClient: GraphQLClient
    abstract val imageLoader: ImageLoader
    abstract val featureFlagManager: FeatureFlagManager
    abstract val appConfigProvider: AppConfigProvider
    abstract val notificationManager: NotificationManager
    abstract val deepLinkHandler: DeepLinkHandler
    abstract val locationManager: LocationManager
    abstract val storeLocator: StoreLocator
    abstract val auditLogger: AuditLogger
    abstract val eventBus: EventBus
    abstract val remoteConfigManager: RemoteConfigManager
"""

# Repositories, data sources, mappers, use cases
for d in DOMAINS:
    c = upper(d)
    component_body += f"    abstract val {d}Repository: {c}Repository\n"
    component_body += f"    abstract val {d}RemoteDataSource: {c}RemoteDataSource\n"
    component_body += f"    abstract val {d}LocalDataSource: {c}LocalDataSource\n"
    component_body += f"    abstract val {d}Mapper: {c}Mapper\n"
    component_body += f"    abstract val get{c}ListUseCase: Get{c}ListUseCase\n"
    component_body += f"    abstract val get{c}DetailUseCase: Get{c}DetailUseCase\n"

component_body += """
    companion object
}
"""

write(f"{BASE}/component/ShopAppComponent.kt", component_imports + component_body)

# ComponentFactory
write(f"{BASE}/component/ComponentFactory.kt", f"""package {PKG}.component

object ComponentFactory {{
    fun create(): ShopAppComponent = ShopAppComponent::class.create()
}}
""")

print("Component written")

# ═══════════════════════════════════════
# BENCHMARK RUNNER
# ═══════════════════════════════════════

write(f"{BASE}/benchmark/KinjectBenchmarkRunner.kt", f"""package {PKG}.benchmark

import {PKG}.component.ComponentFactory

/**
 * kotlin-inject-anvil Runtime Benchmark — Proper Methodology
 *
 * Follows kotlinx-benchmark / JMH best practices:
 * 1. Warmup phase: Multiple iterations to stabilize (excluded from results)
 * 2. Blackhole: Return values consumed to prevent dead code elimination
 * 3. Single component lifecycle: create() once, measure injection patterns
 *    (This matches real-world usage — component is created at app startup)
 * 4. Measurement: Multiple iterations averaged for statistical significance
 */

data class KinjectBenchmarkResult(
    val initTimeNanos: Long,
    val coldInjectionNanos: Map<String, Long>,
    val warmInjectionAvgNanos: Map<String, Long>,
    val totalWarmNanos: Long
)

fun runKinjectBenchmark(warmIterations: Int = 100): KinjectBenchmarkResult {{
    // ── SETUP: Warmup phase (excluded from measurement) ──
    val warmupComponent = ComponentFactory.create()
    val warmupTargets = listOf(
        {{ warmupComponent.homeViewModel }},
        {{ warmupComponent.searchViewModel }},
        {{ warmupComponent.productDetailViewModel }},
        {{ warmupComponent.cartViewModel }},
        {{ warmupComponent.checkoutViewModel }},
        {{ warmupComponent.profileViewModel }},
        {{ warmupComponent.chatViewModel }},
        {{ warmupComponent.orderHistoryViewModel }},
        {{ warmupComponent.analyticsTracker }},
        {{ warmupComponent.productRepository }}
    )
    // Run warmup iterations — results discarded
    repeat(5) {{
        warmupTargets.forEach {{ resolve -> blackhole(resolve()) }}
    }}

    // ── MEASUREMENT 1: Container initialization ──
    val initStart = System.nanoTime()
    val component = ComponentFactory.create()
    val initTime = System.nanoTime() - initStart

    // ── MEASUREMENT 2: Cold injection (first access after fresh component) ──
    val cold = linkedMapOf<String, Long>()
    cold["HomeViewModel"] = measureNanos {{ blackhole(component.homeViewModel) }}
    cold["SearchViewModel"] = measureNanos {{ blackhole(component.searchViewModel) }}
    cold["ProductDetailVM"] = measureNanos {{ blackhole(component.productDetailViewModel) }}
    cold["CartViewModel"] = measureNanos {{ blackhole(component.cartViewModel) }}
    cold["CheckoutViewModel"] = measureNanos {{ blackhole(component.checkoutViewModel) }}
    cold["ProfileViewModel"] = measureNanos {{ blackhole(component.profileViewModel) }}
    cold["ChatViewModel"] = measureNanos {{ blackhole(component.chatViewModel) }}
    cold["OrderHistoryVM"] = measureNanos {{ blackhole(component.orderHistoryViewModel) }}
    cold["AnalyticsTracker"] = measureNanos {{ blackhole(component.analyticsTracker) }}
    cold["ProductRepository"] = measureNanos {{ blackhole(component.productRepository) }}

    // ── MEASUREMENT 3: Warm injection (repeated access) ──
    val warm = linkedMapOf<String, Long>()
    var totalWarm = 0L
    val targets = listOf<Pair<String, () -> Any>>(
        "HomeViewModel" to {{ component.homeViewModel }},
        "SearchViewModel" to {{ component.searchViewModel }},
        "ProductDetailVM" to {{ component.productDetailViewModel }},
        "CartViewModel" to {{ component.cartViewModel }},
        "CheckoutViewModel" to {{ component.checkoutViewModel }},
        "ProfileViewModel" to {{ component.profileViewModel }},
        "ChatViewModel" to {{ component.chatViewModel }},
        "OrderHistoryVM" to {{ component.orderHistoryViewModel }},
        "AnalyticsTracker" to {{ component.analyticsTracker }},
        "ProductRepository" to {{ component.productRepository }}
    )
    for ((name, resolver) in targets) {{
        var sum = 0L
        repeat(warmIterations) {{
            val s = System.nanoTime()
            blackhole(resolver())
            sum += System.nanoTime() - s
        }}
        warm[name] = sum / warmIterations
        totalWarm += sum
    }}

    return KinjectBenchmarkResult(initTime, cold, warm, totalWarm)
}}

// ── Full Benchmark (layered results for detail screen) ──

data class LayerBenchmarkResult(
    val name: String,
    val count: Int,
    val description: String,
    val items: List<Pair<String, Long>>  // name to nanos
)

data class FullBenchmarkResult(
    val layers: List<LayerBenchmarkResult>
)

fun runFullBenchmark(): FullBenchmarkResult {{
    // Warmup
    val warmupComponent = ComponentFactory.create()
    blackhole(warmupComponent.homeViewModel)
    blackhole(warmupComponent.productRepository)

    val layers = mutableListOf<LayerBenchmarkResult>()

    // Layer 1: Component Creation
    val componentItems = mutableListOf<Pair<String, Long>>()
    val componentStart = System.nanoTime()
    val component = ComponentFactory.create()
    componentItems.add("ComponentFactory.create()" to (System.nanoTime() - componentStart))
    layers.add(LayerBenchmarkResult("Component Creation", 1, "DI container initialization", componentItems))

    // Layer 2: ViewModels (13)
    val vmItems = mutableListOf<Pair<String, Long>>()
    vmItems.add("HomeViewModel" to measureNanos {{ blackhole(component.homeViewModel) }})
    vmItems.add("SearchViewModel" to measureNanos {{ blackhole(component.searchViewModel) }})
    vmItems.add("ProductDetailViewModel" to measureNanos {{ blackhole(component.productDetailViewModel) }})
    vmItems.add("CartViewModel" to measureNanos {{ blackhole(component.cartViewModel) }})
    vmItems.add("CheckoutViewModel" to measureNanos {{ blackhole(component.checkoutViewModel) }})
    vmItems.add("ProfileViewModel" to measureNanos {{ blackhole(component.profileViewModel) }})
    vmItems.add("ChatViewModel" to measureNanos {{ blackhole(component.chatViewModel) }})
    vmItems.add("OrderHistoryViewModel" to measureNanos {{ blackhole(component.orderHistoryViewModel) }})
    vmItems.add("SettingsViewModel" to measureNanos {{ blackhole(component.settingsViewModel) }})
    vmItems.add("NotificationsViewModel" to measureNanos {{ blackhole(component.notificationsViewModel) }})
    vmItems.add("OnboardingViewModel" to measureNanos {{ blackhole(component.onboardingViewModel) }})
    vmItems.add("ReviewsViewModel" to measureNanos {{ blackhole(component.reviewsViewModel) }})
    vmItems.add("WishlistViewModel" to measureNanos {{ blackhole(component.wishlistViewModel) }})
    layers.add(LayerBenchmarkResult("ViewModels", 13, "Feature-level presentation layer", vmItems))

    // Layer 3: Core Singletons (14)
    val coreItems = mutableListOf<Pair<String, Long>>()
    coreItems.add("HttpClient" to measureNanos {{ blackhole(component.httpClient) }})
    coreItems.add("AuthManager" to measureNanos {{ blackhole(component.authManager) }})
    coreItems.add("TokenStorage" to measureNanos {{ blackhole(component.tokenStorage) }})
    coreItems.add("SessionManager" to measureNanos {{ blackhole(component.sessionManager) }})
    coreItems.add("AnalyticsTracker" to measureNanos {{ blackhole(component.analyticsTracker) }})
    coreItems.add("CrashReporter" to measureNanos {{ blackhole(component.crashReporter) }})
    coreItems.add("DatabaseManager" to measureNanos {{ blackhole(component.databaseManager) }})
    coreItems.add("PreferencesManager" to measureNanos {{ blackhole(component.preferencesManager) }})
    coreItems.add("SecureStorage" to measureNanos {{ blackhole(component.secureStorage) }})
    coreItems.add("CacheManager" to measureNanos {{ blackhole(component.cacheManager) }})
    coreItems.add("AppLogger" to measureNanos {{ blackhole(component.appLogger) }})
    coreItems.add("TokenProvider" to measureNanos {{ blackhole(component.tokenProvider) }})
    coreItems.add("NetworkLogger" to measureNanos {{ blackhole(component.networkLogger) }})
    coreItems.add("CachePolicy" to measureNanos {{ blackhole(component.cachePolicy) }})
    layers.add(LayerBenchmarkResult("Core Singletons", 14, "Shared infrastructure services", coreItems))

    // Layer 4: Core Services (12)
    val svcItems = mutableListOf<Pair<String, Long>>()
    svcItems.add("AuthInterceptor" to measureNanos {{ blackhole(component.authInterceptor) }})
    svcItems.add("RateLimiter" to measureNanos {{ blackhole(component.rateLimiter) }})
    svcItems.add("WebSocketManager" to measureNanos {{ blackhole(component.webSocketManager) }})
    svcItems.add("GraphQLClient" to measureNanos {{ blackhole(component.graphQLClient) }})
    svcItems.add("ImageLoader" to measureNanos {{ blackhole(component.imageLoader) }})
    svcItems.add("FeatureFlagManager" to measureNanos {{ blackhole(component.featureFlagManager) }})
    svcItems.add("AppConfigProvider" to measureNanos {{ blackhole(component.appConfigProvider) }})
    svcItems.add("NotificationManager" to measureNanos {{ blackhole(component.notificationManager) }})
    svcItems.add("DeepLinkHandler" to measureNanos {{ blackhole(component.deepLinkHandler) }})
    svcItems.add("LocationManager" to measureNanos {{ blackhole(component.locationManager) }})
    svcItems.add("StoreLocator" to measureNanos {{ blackhole(component.storeLocator) }})
    svcItems.add("AuditLogger" to measureNanos {{ blackhole(component.auditLogger) }})
    layers.add(LayerBenchmarkResult("Core Services", 12, "Application-level services", svcItems))

    // Layer 5: Repositories (14)
    val repoItems = mutableListOf<Pair<String, Long>>()
    repoItems.add("ProductRepository" to measureNanos {{ blackhole(component.productRepository) }})
    repoItems.add("UserRepository" to measureNanos {{ blackhole(component.userRepository) }})
    repoItems.add("CartRepository" to measureNanos {{ blackhole(component.cartRepository) }})
    repoItems.add("OrderRepository" to measureNanos {{ blackhole(component.orderRepository) }})
    repoItems.add("PaymentRepository" to measureNanos {{ blackhole(component.paymentRepository) }})
    repoItems.add("ChatRepository" to measureNanos {{ blackhole(component.chatRepository) }})
    repoItems.add("SearchRepository" to measureNanos {{ blackhole(component.searchRepository) }})
    repoItems.add("ReviewRepository" to measureNanos {{ blackhole(component.reviewRepository) }})
    repoItems.add("CategoryRepository" to measureNanos {{ blackhole(component.categoryRepository) }})
    repoItems.add("AddressRepository" to measureNanos {{ blackhole(component.addressRepository) }})
    repoItems.add("WishlistRepository" to measureNanos {{ blackhole(component.wishlistRepository) }})
    repoItems.add("PromotionRepository" to measureNanos {{ blackhole(component.promotionRepository) }})
    repoItems.add("ShippingRepository" to measureNanos {{ blackhole(component.shippingRepository) }})
    repoItems.add("FeedRepository" to measureNanos {{ blackhole(component.feedRepository) }})
    layers.add(LayerBenchmarkResult("Repositories", 14, "Data access abstraction layer", repoItems))

    // Layer 6: RemoteDataSources (14)
    val remoteItems = mutableListOf<Pair<String, Long>>()
    remoteItems.add("ProductRemoteDataSource" to measureNanos {{ blackhole(component.productRemoteDataSource) }})
    remoteItems.add("UserRemoteDataSource" to measureNanos {{ blackhole(component.userRemoteDataSource) }})
    remoteItems.add("CartRemoteDataSource" to measureNanos {{ blackhole(component.cartRemoteDataSource) }})
    remoteItems.add("OrderRemoteDataSource" to measureNanos {{ blackhole(component.orderRemoteDataSource) }})
    remoteItems.add("PaymentRemoteDataSource" to measureNanos {{ blackhole(component.paymentRemoteDataSource) }})
    remoteItems.add("ChatRemoteDataSource" to measureNanos {{ blackhole(component.chatRemoteDataSource) }})
    remoteItems.add("SearchRemoteDataSource" to measureNanos {{ blackhole(component.searchRemoteDataSource) }})
    remoteItems.add("ReviewRemoteDataSource" to measureNanos {{ blackhole(component.reviewRemoteDataSource) }})
    remoteItems.add("CategoryRemoteDataSource" to measureNanos {{ blackhole(component.categoryRemoteDataSource) }})
    remoteItems.add("AddressRemoteDataSource" to measureNanos {{ blackhole(component.addressRemoteDataSource) }})
    remoteItems.add("WishlistRemoteDataSource" to measureNanos {{ blackhole(component.wishlistRemoteDataSource) }})
    remoteItems.add("PromotionRemoteDataSource" to measureNanos {{ blackhole(component.promotionRemoteDataSource) }})
    remoteItems.add("ShippingRemoteDataSource" to measureNanos {{ blackhole(component.shippingRemoteDataSource) }})
    remoteItems.add("FeedRemoteDataSource" to measureNanos {{ blackhole(component.feedRemoteDataSource) }})
    layers.add(LayerBenchmarkResult("RemoteDataSources", 14, "Network API data sources", remoteItems))

    // Layer 7: LocalDataSources (14)
    val localItems = mutableListOf<Pair<String, Long>>()
    localItems.add("ProductLocalDataSource" to measureNanos {{ blackhole(component.productLocalDataSource) }})
    localItems.add("UserLocalDataSource" to measureNanos {{ blackhole(component.userLocalDataSource) }})
    localItems.add("CartLocalDataSource" to measureNanos {{ blackhole(component.cartLocalDataSource) }})
    localItems.add("OrderLocalDataSource" to measureNanos {{ blackhole(component.orderLocalDataSource) }})
    localItems.add("PaymentLocalDataSource" to measureNanos {{ blackhole(component.paymentLocalDataSource) }})
    localItems.add("ChatLocalDataSource" to measureNanos {{ blackhole(component.chatLocalDataSource) }})
    localItems.add("SearchLocalDataSource" to measureNanos {{ blackhole(component.searchLocalDataSource) }})
    localItems.add("ReviewLocalDataSource" to measureNanos {{ blackhole(component.reviewLocalDataSource) }})
    localItems.add("CategoryLocalDataSource" to measureNanos {{ blackhole(component.categoryLocalDataSource) }})
    localItems.add("AddressLocalDataSource" to measureNanos {{ blackhole(component.addressLocalDataSource) }})
    localItems.add("WishlistLocalDataSource" to measureNanos {{ blackhole(component.wishlistLocalDataSource) }})
    localItems.add("PromotionLocalDataSource" to measureNanos {{ blackhole(component.promotionLocalDataSource) }})
    localItems.add("ShippingLocalDataSource" to measureNanos {{ blackhole(component.shippingLocalDataSource) }})
    localItems.add("FeedLocalDataSource" to measureNanos {{ blackhole(component.feedLocalDataSource) }})
    layers.add(LayerBenchmarkResult("LocalDataSources", 14, "Local/cached data sources", localItems))

    // Layer 8: Mappers (14)
    val mapperItems = mutableListOf<Pair<String, Long>>()
    mapperItems.add("ProductMapper" to measureNanos {{ blackhole(component.productMapper) }})
    mapperItems.add("UserMapper" to measureNanos {{ blackhole(component.userMapper) }})
    mapperItems.add("CartMapper" to measureNanos {{ blackhole(component.cartMapper) }})
    mapperItems.add("OrderMapper" to measureNanos {{ blackhole(component.orderMapper) }})
    mapperItems.add("PaymentMapper" to measureNanos {{ blackhole(component.paymentMapper) }})
    mapperItems.add("ChatMapper" to measureNanos {{ blackhole(component.chatMapper) }})
    mapperItems.add("SearchMapper" to measureNanos {{ blackhole(component.searchMapper) }})
    mapperItems.add("ReviewMapper" to measureNanos {{ blackhole(component.reviewMapper) }})
    mapperItems.add("CategoryMapper" to measureNanos {{ blackhole(component.categoryMapper) }})
    mapperItems.add("AddressMapper" to measureNanos {{ blackhole(component.addressMapper) }})
    mapperItems.add("WishlistMapper" to measureNanos {{ blackhole(component.wishlistMapper) }})
    mapperItems.add("PromotionMapper" to measureNanos {{ blackhole(component.promotionMapper) }})
    mapperItems.add("ShippingMapper" to measureNanos {{ blackhole(component.shippingMapper) }})
    mapperItems.add("FeedMapper" to measureNanos {{ blackhole(component.feedMapper) }})
    layers.add(LayerBenchmarkResult("Mappers", 14, "Data transformation layer", mapperItems))

    // Layer 9: UseCases (28)
    val ucItems = mutableListOf<Pair<String, Long>>()
    ucItems.add("GetProductListUseCase" to measureNanos {{ blackhole(component.getProductListUseCase) }})
    ucItems.add("GetProductDetailUseCase" to measureNanos {{ blackhole(component.getProductDetailUseCase) }})
    ucItems.add("GetUserListUseCase" to measureNanos {{ blackhole(component.getUserListUseCase) }})
    ucItems.add("GetUserDetailUseCase" to measureNanos {{ blackhole(component.getUserDetailUseCase) }})
    ucItems.add("GetCartListUseCase" to measureNanos {{ blackhole(component.getCartListUseCase) }})
    ucItems.add("GetCartDetailUseCase" to measureNanos {{ blackhole(component.getCartDetailUseCase) }})
    ucItems.add("GetOrderListUseCase" to measureNanos {{ blackhole(component.getOrderListUseCase) }})
    ucItems.add("GetOrderDetailUseCase" to measureNanos {{ blackhole(component.getOrderDetailUseCase) }})
    ucItems.add("GetPaymentListUseCase" to measureNanos {{ blackhole(component.getPaymentListUseCase) }})
    ucItems.add("GetPaymentDetailUseCase" to measureNanos {{ blackhole(component.getPaymentDetailUseCase) }})
    ucItems.add("GetChatListUseCase" to measureNanos {{ blackhole(component.getChatListUseCase) }})
    ucItems.add("GetChatDetailUseCase" to measureNanos {{ blackhole(component.getChatDetailUseCase) }})
    ucItems.add("GetSearchListUseCase" to measureNanos {{ blackhole(component.getSearchListUseCase) }})
    ucItems.add("GetSearchDetailUseCase" to measureNanos {{ blackhole(component.getSearchDetailUseCase) }})
    ucItems.add("GetReviewListUseCase" to measureNanos {{ blackhole(component.getReviewListUseCase) }})
    ucItems.add("GetReviewDetailUseCase" to measureNanos {{ blackhole(component.getReviewDetailUseCase) }})
    ucItems.add("GetCategoryListUseCase" to measureNanos {{ blackhole(component.getCategoryListUseCase) }})
    ucItems.add("GetCategoryDetailUseCase" to measureNanos {{ blackhole(component.getCategoryDetailUseCase) }})
    ucItems.add("GetAddressListUseCase" to measureNanos {{ blackhole(component.getAddressListUseCase) }})
    ucItems.add("GetAddressDetailUseCase" to measureNanos {{ blackhole(component.getAddressDetailUseCase) }})
    ucItems.add("GetWishlistListUseCase" to measureNanos {{ blackhole(component.getWishlistListUseCase) }})
    ucItems.add("GetWishlistDetailUseCase" to measureNanos {{ blackhole(component.getWishlistDetailUseCase) }})
    ucItems.add("GetPromotionListUseCase" to measureNanos {{ blackhole(component.getPromotionListUseCase) }})
    ucItems.add("GetPromotionDetailUseCase" to measureNanos {{ blackhole(component.getPromotionDetailUseCase) }})
    ucItems.add("GetShippingListUseCase" to measureNanos {{ blackhole(component.getShippingListUseCase) }})
    ucItems.add("GetShippingDetailUseCase" to measureNanos {{ blackhole(component.getShippingDetailUseCase) }})
    ucItems.add("GetFeedListUseCase" to measureNanos {{ blackhole(component.getFeedListUseCase) }})
    ucItems.add("GetFeedDetailUseCase" to measureNanos {{ blackhole(component.getFeedDetailUseCase) }})
    layers.add(LayerBenchmarkResult("UseCases", 28, "Business logic / domain layer", ucItems))

    return FullBenchmarkResult(layers)
}}

// Blackhole: prevents dead code elimination.
@Suppress("UNUSED_PARAMETER")
private fun blackhole(value: Any?) {{
    // Intentionally empty — the function call itself prevents DCE
}}

private inline fun measureNanos(block: () -> Unit): Long {{
    val s = System.nanoTime()
    block()
    return System.nanoTime() - s
}}
""")

print("Benchmark runner written")
print()
print("Done: benchmark-kinject-large rewritten as production-quality kotlin-inject-anvil app")
print(f"  Domains: {len(DOMAINS)}")
print(f"  ViewModels: {len(vms)} (extending ViewModel(), @Inject)")
print(f"  @SingleIn singletons: core + repositories")
print(f"  @MergeComponent with @Provides bindings + @ContributesBinding")
