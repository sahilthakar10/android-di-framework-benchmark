#!/usr/bin/env python3
"""
Rewrites benchmark-metro-large as production-quality Metro app.
Same architecture as hilt-large and koin-large. Uses Metro DI patterns:
  - @Inject on constructors
  - @SingleIn(AppScope::class) for singletons
  - @DependencyGraph(AppScope::class) for the graph
  - @Provides in graph interface for config objects
"""

import os
import shutil

BASE = "/Users/sahilthakar/AndroidStudioProjects/BenchMarking/benchmark-metro-large/src/main/kotlin/com/codeint/shopapp/metro"
PKG = "com.codeint.shopapp.metro"

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

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface TokenProvider {{ fun getAccessToken(): String; fun getRefreshToken(): String; fun isExpired(): Boolean }}
interface NetworkLogger {{ fun logRequest(method: String, url: String, status: Int); fun logError(url: String, error: String) }}
interface CachePolicy {{ fun isCacheable(url: String): Boolean; fun getTtl(url: String): Long }}

@SingleIn(AppScope::class) class RealTokenProvider @Inject constructor() : TokenProvider {{
    override fun getAccessToken() = "mock_access_token"; override fun getRefreshToken() = "mock_refresh_token"; override fun isExpired() = false
}}
@SingleIn(AppScope::class) class RealNetworkLogger @Inject constructor() : NetworkLogger {{
    override fun logRequest(method: String, url: String, status: Int) {{}}; override fun logError(url: String, error: String) {{}}
}}
@SingleIn(AppScope::class) class RealCachePolicy @Inject constructor() : CachePolicy {{
    override fun isCacheable(url: String) = url.contains("/products"); override fun getTtl(url: String) = 300_000L
}}
@SingleIn(AppScope::class) class AuthInterceptor @Inject constructor(private val tokenProvider: TokenProvider) {{ fun intercept(url: String) = mapOf("Auth" to tokenProvider.getAccessToken()) }}
@SingleIn(AppScope::class) class CacheInterceptor @Inject constructor(private val cachePolicy: CachePolicy) {{ fun shouldCache(url: String) = cachePolicy.isCacheable(url) }}
@SingleIn(AppScope::class) class LoggingInterceptor @Inject constructor(private val networkLogger: NetworkLogger) {{ fun log(m: String, u: String, s: Int) {{ networkLogger.logRequest(m, u, s) }} }}
@SingleIn(AppScope::class) class RateLimiter @Inject constructor(private val config: RetryPolicy) {{ fun shouldThrottle(endpoint: String) = false }}
@SingleIn(AppScope::class) class WebSocketManager @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {{ fun connect(ch: String) {{}}; fun send(msg: String) {{}} }}
@SingleIn(AppScope::class) class GraphQLClient @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) {{ fun query(q: String) = "{{}}"; fun mutate(m: String) = "{{}}" }}
@SingleIn(AppScope::class) class FileUploader @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {{ fun upload(f: String, e: String) = "id_123" }}
""")

write(f"{BASE}/core/auth/AuthServices.kt", f"""package {PKG}.core.auth

import {PKG}.core.analytics.AnalyticsTracker
import {PKG}.core.storage.PreferencesManager
import {PKG}.core.storage.SecureStorage
import {PKG}.core.network.HttpClient
import {PKG}.core.network.TokenProvider
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface AuthManager {{ fun login(email: String, password: String): Boolean; fun logout(); fun isLoggedIn(): Boolean }}
interface TokenStorage {{ fun saveTokens(a: String, r: String); fun getAccessToken(): String?; fun hasValidToken(): Boolean; fun clear() }}
interface SessionManager {{ fun startSession(userId: String); fun getCurrentUserId(): String?; fun invalidate() }}

@SingleIn(AppScope::class) class RealAuthManager @Inject constructor(private val tokenStorage: TokenStorage, private val sessionManager: SessionManager, private val analytics: AnalyticsTracker) : AuthManager {{
    override fun login(email: String, password: String): Boolean {{ analytics.track("login"); return true }}
    override fun logout() {{ tokenStorage.clear(); sessionManager.invalidate() }}; override fun isLoggedIn() = tokenStorage.hasValidToken()
}}
@SingleIn(AppScope::class) class RealTokenStorage @Inject constructor(private val secureStorage: SecureStorage) : TokenStorage {{
    override fun saveTokens(a: String, r: String) {{ secureStorage.put("token", a) }}; override fun getAccessToken() = secureStorage.get("token")
    override fun hasValidToken() = getAccessToken() != null; override fun clear() {{ secureStorage.remove("token") }}
}}
@SingleIn(AppScope::class) class RealSessionManager @Inject constructor(private val prefs: PreferencesManager) : SessionManager {{
    override fun startSession(userId: String) {{ prefs.putString("user", userId) }}; override fun getCurrentUserId() = prefs.getString("user"); override fun invalidate() {{ prefs.remove("user") }}
}}
class BiometricAuthProvider @Inject constructor(private val secureStorage: SecureStorage) {{ fun isAvailable() = true }}
class OAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {{ fun authorizeWithGoogle() = true }}
class PasswordValidator @Inject constructor() {{ fun validate(password: String) = password.length >= 8 }}
class TwoFactorAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {{ fun requestCode(phone: String) = true }}
""")

write(f"{BASE}/core/analytics/AnalyticsServices.kt", f"""package {PKG}.core.analytics

import {PKG}.core.storage.PreferencesManager
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface AnalyticsTracker {{ fun track(event: String, params: Map<String, Any> = emptyMap()); fun setUserId(userId: String); fun screen(name: String) }}
interface CrashReporter {{ fun report(t: Throwable); fun log(msg: String); fun setUserId(userId: String) }}

@SingleIn(AppScope::class) class EventBus @Inject constructor() {{ private val listeners = mutableListOf<(AnalyticsEvent) -> Unit>(); fun post(e: AnalyticsEvent) {{ listeners.forEach {{ it(e) }} }} }}
@SingleIn(AppScope::class) class UserPropertyTracker @Inject constructor() {{ private val props = mutableMapOf<String, Any>(); fun set(k: String, v: Any) {{ props[k] = v }}; fun get(k: String) = props[k] }}
@SingleIn(AppScope::class) class RemoteConfigManager @Inject constructor() {{ fun getString(k: String, d: String) = d; fun getBoolean(k: String, d: Boolean) = d; fun getLong(k: String, d: Long) = d }}
@SingleIn(AppScope::class) class RealAnalyticsTracker @Inject constructor(private val eventBus: EventBus, private val upt: UserPropertyTracker) : AnalyticsTracker {{
    override fun track(event: String, params: Map<String, Any>) {{ eventBus.post(AnalyticsEvent(event, params)) }}
    override fun setUserId(userId: String) {{ upt.set("user_id", userId) }}; override fun screen(name: String) {{ track("screen_view", mapOf("screen" to name)) }}
}}
@SingleIn(AppScope::class) class RealCrashReporter @Inject constructor() : CrashReporter {{ override fun report(t: Throwable) {{}}; override fun log(msg: String) {{}}; override fun setUserId(userId: String) {{}} }}
class PerformanceMonitor @Inject constructor() {{ fun startTrace(name: String) = TraceHandle(name, System.nanoTime()) }}
class ABTestManager @Inject constructor(private val rc: RemoteConfigManager) {{ fun getVariant(id: String) = rc.getString("exp_$id", "control") }}
class ConsentManager @Inject constructor(private val prefs: PreferencesManager) {{ fun hasAnalyticsConsent() = true }}

data class AnalyticsEvent(val name: String, val params: Map<String, Any>)
data class TraceHandle(val name: String, val startTime: Long)
""")

write(f"{BASE}/core/storage/StorageServices.kt", f"""package {PKG}.core.storage

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface DatabaseManager {{ fun query(table: String, where: String = ""): List<Map<String, Any>>; fun insert(table: String, values: Map<String, Any>): Long; fun update(table: String, values: Map<String, Any>, where: String): Int; fun delete(table: String, where: String): Int; fun transaction(block: () -> Unit) }}
interface PreferencesManager {{ fun putString(k: String, v: String); fun getString(k: String): String?; fun putBoolean(k: String, v: Boolean); fun getBoolean(k: String, default: Boolean = false): Boolean; fun putLong(k: String, v: Long); fun getLong(k: String, default: Long = 0): Long; fun remove(k: String) }}
interface SecureStorage {{ fun put(k: String, v: String); fun get(k: String): String?; fun remove(k: String) }}
interface CacheManager {{ fun get(k: String): Any?; fun put(k: String, v: Any, ttlMs: Long = 300_000); fun evict(k: String); fun clear() }}

@SingleIn(AppScope::class) class RealDatabaseManager @Inject constructor() : DatabaseManager {{
    override fun query(table: String, where: String) = emptyList<Map<String, Any>>(); override fun insert(table: String, values: Map<String, Any>) = 1L
    override fun update(table: String, values: Map<String, Any>, where: String) = 1; override fun delete(table: String, where: String) = 1; override fun transaction(block: () -> Unit) {{ block() }}
}}
@SingleIn(AppScope::class) class RealPreferencesManager @Inject constructor() : PreferencesManager {{
    private val store = mutableMapOf<String, Any>()
    override fun putString(k: String, v: String) {{ store[k] = v }}; override fun getString(k: String) = store[k] as? String
    override fun putBoolean(k: String, v: Boolean) {{ store[k] = v }}; override fun getBoolean(k: String, default: Boolean) = store[k] as? Boolean ?: default
    override fun putLong(k: String, v: Long) {{ store[k] = v }}; override fun getLong(k: String, default: Long) = store[k] as? Long ?: default; override fun remove(k: String) {{ store.remove(k) }}
}}
@SingleIn(AppScope::class) class RealSecureStorage @Inject constructor() : SecureStorage {{ private val store = mutableMapOf<String, String>(); override fun put(k: String, v: String) {{ store[k] = v }}; override fun get(k: String) = store[k]; override fun remove(k: String) {{ store.remove(k) }} }}
@SingleIn(AppScope::class) class RealCacheManager @Inject constructor(private val prefs: PreferencesManager) : CacheManager {{
    private val cache = mutableMapOf<String, CacheEntry>()
    override fun get(k: String): Any? = cache[k]?.takeIf {{ !it.isExpired() }}?.value
    override fun put(k: String, v: Any, ttlMs: Long) {{ cache[k] = CacheEntry(v, System.currentTimeMillis() + ttlMs) }}
    override fun evict(k: String) {{ cache.remove(k) }}; override fun clear() {{ cache.clear() }}
}}
class FileManager @Inject constructor() {{ fun read(p: String) = ByteArray(0); fun write(p: String, d: ByteArray) {{}} }}
class DownloadManager @Inject constructor(private val fm: FileManager) {{ fun download(url: String, dest: String) = dest }}
data class CacheEntry(val value: Any, val expiresAt: Long) {{ fun isExpired() = System.currentTimeMillis() > expiresAt }}
""")

write(f"{BASE}/core/config/ConfigServices.kt", f"""package {PKG}.core.config

import {PKG}.core.analytics.RemoteConfigManager
import {PKG}.core.storage.PreferencesManager
import dev.zacsweers.metro.Inject

class FeatureFlagManager @Inject constructor(private val rc: RemoteConfigManager) {{ fun isEnabled(flag: String) = rc.getBoolean("ff_$flag", false) }}
class AppConfigProvider @Inject constructor(private val rc: RemoteConfigManager) {{ fun getApiVersion() = rc.getString("api_version", "v3") }}
class ThemeManager @Inject constructor(private val prefs: PreferencesManager) {{ fun isDarkMode() = prefs.getBoolean("dark_mode", false) }}
class LocaleManager @Inject constructor(private val prefs: PreferencesManager) {{ fun getCurrentLocale() = prefs.getString("locale") ?: "en" }}
class EnvironmentManager @Inject constructor() {{ fun getEnvironment() = "production" }}
""")

write(f"{BASE}/core/logging/LoggingServices.kt", f"""package {PKG}.core.logging

import {PKG}.core.analytics.CrashReporter
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface AppLogger {{ fun debug(tag: String, msg: String); fun info(tag: String, msg: String); fun warn(tag: String, msg: String); fun error(tag: String, msg: String, t: Throwable? = null) }}
@SingleIn(AppScope::class) class RealAppLogger @Inject constructor(private val crashReporter: CrashReporter) : AppLogger {{
    override fun debug(tag: String, msg: String) {{}}; override fun info(tag: String, msg: String) {{}}
    override fun warn(tag: String, msg: String) {{}}; override fun error(tag: String, msg: String, t: Throwable?) {{ t?.let {{ crashReporter.report(it) }} }}
}}
class AuditLogger @Inject constructor(private val appLogger: AppLogger) {{ fun logUserAction(a: String) {{ appLogger.info("AUDIT", a) }} }}
""")

write(f"{BASE}/core/image/ImageServices.kt", f"""package {PKG}.core.image

import {PKG}.core.network.HttpClient
import {PKG}.core.storage.CacheManager
import dev.zacsweers.metro.Inject

class ImageLoader @Inject constructor(private val httpClient: HttpClient, private val cacheManager: CacheManager) {{ fun load(url: String) = ByteArray(0) }}
class ImageProcessor @Inject constructor() {{ fun resize(data: ByteArray, w: Int, h: Int) = data }}
class ThumbnailGenerator @Inject constructor(private val ip: ImageProcessor) {{ fun generate(data: ByteArray, size: Int) = ip.resize(data, size, size) }}
""")

write(f"{BASE}/core/notification/NotificationServices.kt", f"""package {PKG}.core.notification

import {PKG}.core.storage.PreferencesManager
import {PKG}.core.auth.SessionManager
import {PKG}.core.config.FeatureFlagManager
import {PKG}.core.analytics.AnalyticsTracker
import dev.zacsweers.metro.Inject

class NotificationManager @Inject constructor(private val prefs: PreferencesManager) {{ fun isEnabled() = prefs.getBoolean("notif", true) }}
class PushTokenManager @Inject constructor(private val sm: SessionManager) {{ fun getToken(): String? = null }}
class DeepLinkHandler @Inject constructor() {{ fun handle(uri: String) = DeepLinkResult(if (uri.contains("/product/")) "product" else "home", emptyMap()) }}
class InAppMessageManager @Inject constructor(private val ff: FeatureFlagManager, private val at: AnalyticsTracker) {{ fun showBanner(msg: String, type: String) {{ at.track("banner") }} }}
data class DeepLinkResult(val destination: String, val params: Map<String, String>)
""")

write(f"{BASE}/core/location/LocationServices.kt", f"""package {PKG}.core.location

import {PKG}.core.storage.PreferencesManager
import dev.zacsweers.metro.Inject

class LocationManager @Inject constructor(private val prefs: PreferencesManager) {{ fun getLastKnownLocation(): Location? = null }}
class GeocodingService @Inject constructor() {{ fun getAddress(lat: Double, lng: Double) = "123 Main St" }}
class StoreLocator @Inject constructor(private val lm: LocationManager, private val gc: GeocodingService) {{ fun findNearbyStores() = listOf(Store("Store 1", 37.78, -122.41, 1.2)) }}
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
import dev.zacsweers.metro.Inject

class {cap}RemoteDataSource @Inject constructor(private val httpClient: HttpClient, private val apiParser: ApiResponseParser, private val authInterceptor: AuthInterceptor, private val rateLimiter: RateLimiter) {{
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
import dev.zacsweers.metro.Inject

class {cap}LocalDataSource @Inject constructor(private val db: DatabaseManager, private val cache: CacheManager) {{
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
import dev.zacsweers.metro.Inject

class {cap}Mapper @Inject constructor() {{
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
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

@SingleIn(AppScope::class) class OfflineFirst{cap}Repository @Inject constructor(
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
import dev.zacsweers.metro.Inject

class Get{cap}ListUseCase @Inject constructor(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{
    fun execute(page: Int = 0) = repo.getAll().also {{ analytics.track("get_{domain}_list") }}
}}
class Get{cap}DetailUseCase @Inject constructor(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{
    fun execute(id: String) = repo.getById(id).also {{ analytics.track("get_{domain}_detail") }}
}}
class Create{cap}UseCase @Inject constructor(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(m: {cap}DomainModel) = repo.create(m) }}
class Update{cap}UseCase @Inject constructor(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(id: String, m: {cap}DomainModel) = repo.update(id, m) }}
class Delete{cap}UseCase @Inject constructor(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute(id: String) = repo.delete(id) }}
class Search{cap}UseCase @Inject constructor(private val repo: {cap}Repository, private val analytics: AnalyticsTracker) {{ fun execute(q: String, p: Int = 0) = repo.search(q, p) }}
class Validate{cap}UseCase @Inject constructor(private val logger: AppLogger) {{
    fun execute(m: {cap}DomainModel): ValidationResult {{ val e = mutableListOf<String>(); if (m.name.isBlank()) e.add("Name required"); return ValidationResult(e.isEmpty(), e) }}
}}
class Refresh{cap}CacheUseCase @Inject constructor(private val repo: {cap}Repository, private val logger: AppLogger) {{ fun execute() {{ repo.clearCache(); repo.getAll() }} }}
class Get{cap}CountUseCase @Inject constructor(private val repo: {cap}Repository) {{ fun execute() = repo.getAll().totalCount }}
class Filter{cap}UseCase @Inject constructor(private val repo: {cap}Repository) {{ fun execute(p: ({cap}DomainModel) -> Boolean) = repo.getAll().items.filter(p) }}

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
    imports = f"import androidx.lifecycle.ViewModel\nimport androidx.lifecycle.viewModelScope\nimport {PKG}.core.analytics.AnalyticsTracker\nimport dev.zacsweers.metro.Inject\n"
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
class {vm_name} @Inject constructor(
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
# GRAPH — @DependencyGraph with @Provides for config objects
# ═══════════════════════════════════════

graph_imports = f"""package {PKG}.graph

import dev.zacsweers.metro.*
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
    graph_imports += f"import {PKG}.data.{d}.{c}Repository\nimport {PKG}.data.{d}.OfflineFirst{c}Repository\n"
    graph_imports += f"import {PKG}.data.{d}.remote.{c}RemoteDataSource\nimport {PKG}.data.{d}.local.{c}LocalDataSource\nimport {PKG}.data.{d}.mapper.{c}Mapper\n"
    graph_imports += f"import {PKG}.domain.{d}.*\n"

graph_imports += f"""import {PKG}.feature.home.HomeViewModel
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

graph_body = """
@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
interface ShopAppGraph {

    // Config objects via @Provides
    @Provides fun provideHttpClient(): HttpClient = HttpClient("https://api.shopapp.com", 30_000)
    @Provides fun provideApiResponseParser(): ApiResponseParser = ApiResponseParser()
    @Provides fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor()
    @Provides fun provideRetryPolicy(): RetryPolicy = RetryPolicy(3, 1000)
    @Provides fun provideSslPinningConfig(): SslPinningConfig = SslPinningConfig(listOf("sha256/abc123"))

    // Interface bindings via @Provides
    @Provides fun bindTokenProvider(impl: RealTokenProvider): TokenProvider = impl
    @Provides fun bindNetworkLogger(impl: RealNetworkLogger): NetworkLogger = impl
    @Provides fun bindCachePolicy(impl: RealCachePolicy): CachePolicy = impl
    @Provides fun bindAuthManager(impl: RealAuthManager): AuthManager = impl
    @Provides fun bindTokenStorage(impl: RealTokenStorage): TokenStorage = impl
    @Provides fun bindSessionManager(impl: RealSessionManager): SessionManager = impl
    @Provides fun bindAnalyticsTracker(impl: RealAnalyticsTracker): AnalyticsTracker = impl
    @Provides fun bindCrashReporter(impl: RealCrashReporter): CrashReporter = impl
    @Provides fun bindDatabaseManager(impl: RealDatabaseManager): DatabaseManager = impl
    @Provides fun bindPreferencesManager(impl: RealPreferencesManager): PreferencesManager = impl
    @Provides fun bindSecureStorage(impl: RealSecureStorage): SecureStorage = impl
    @Provides fun bindCacheManager(impl: RealCacheManager): CacheManager = impl
    @Provides fun bindAppLogger(impl: RealAppLogger): AppLogger = impl
"""

# Repository bindings
for d in DOMAINS:
    c = upper(d)
    graph_body += f"    @Provides fun bind{c}Repository(impl: OfflineFirst{c}Repository): {c}Repository = impl\n"

# Accessors for all classes
graph_body += "\n    // ── Accessors ──\n"

# ViewModels
for _, vm_name, _, _ in vms:
    prop = vm_name[0].lower() + vm_name[1:]
    graph_body += f"    val {prop}: {vm_name}\n"

# Core services
graph_body += """
    val authInterceptor: AuthInterceptor
    val rateLimiter: RateLimiter
    val webSocketManager: WebSocketManager
    val graphQLClient: GraphQLClient
    val imageLoader: ImageLoader
    val featureFlagManager: FeatureFlagManager
    val appConfigProvider: AppConfigProvider
    val notificationManager: NotificationManager
    val deepLinkHandler: DeepLinkHandler
    val locationManager: LocationManager
    val storeLocator: StoreLocator
    val auditLogger: AuditLogger
    val eventBus: EventBus
    val remoteConfigManager: RemoteConfigManager
"""

# Repositories, data sources, mappers, use cases
for d in DOMAINS:
    c = upper(d)
    graph_body += f"    val {d}Repository: {c}Repository\n"
    graph_body += f"    val {d}RemoteDataSource: {c}RemoteDataSource\n"
    graph_body += f"    val {d}LocalDataSource: {c}LocalDataSource\n"
    graph_body += f"    val {d}Mapper: {c}Mapper\n"
    graph_body += f"    val get{c}ListUseCase: Get{c}ListUseCase\n"
    graph_body += f"    val get{c}DetailUseCase: Get{c}DetailUseCase\n"

graph_body += "}\n"

write(f"{BASE}/graph/ShopAppGraph.kt", graph_imports + graph_body)

# GraphFactory
write(f"{BASE}/graph/GraphFactory.kt", f"""package {PKG}.graph

import dev.zacsweers.metro.createGraph

object GraphFactory {{
    fun create(): ShopAppGraph = createGraph<ShopAppGraph>()
}}
""")

print("Graph written")
print()
print("✅ benchmark-metro-large rewritten as production-quality Metro app")
print(f"  Domains: {len(DOMAINS)}")
print(f"  ViewModels: {len(vms)} (extending ViewModel(), @Inject)")
print(f"  @SingleIn singletons: core + repositories")
print(f"  @DependencyGraph with @Provides bindings")
