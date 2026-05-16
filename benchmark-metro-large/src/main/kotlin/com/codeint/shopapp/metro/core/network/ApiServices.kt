package com.codeint.shopapp.metro.core.network

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope
@SingleIn(AppScope::class)
class AuthInterceptor @Inject constructor(private val tokenProvider: TokenProvider) {
    fun intercept(url: String): Map<String, String> = mapOf("Authorization" to "Bearer ${tokenProvider.getAccessToken()}")
}

@SingleIn(AppScope::class)
class CacheInterceptor @Inject constructor(private val cachePolicy: CachePolicy) {
    fun shouldCache(url: String): Boolean = cachePolicy.isCacheable(url)
}

@SingleIn(AppScope::class)
class LoggingInterceptor @Inject constructor(private val networkLogger: NetworkLogger) {
    fun log(method: String, url: String, status: Int) { networkLogger.logRequest(method, url, status) }
}

@SingleIn(AppScope::class)
class RateLimiter @Inject constructor(private val config: RetryPolicy) {
    private val requestCounts = mutableMapOf<String, Int>()
    fun shouldThrottle(endpoint: String): Boolean = (requestCounts[endpoint] ?: 0) > 100
}

@SingleIn(AppScope::class)
class NetworkLogger @Inject constructor() {
    fun logRequest(method: String, url: String, status: Int) {}
    fun logError(url: String, error: String) {}
}

@SingleIn(AppScope::class)
class CachePolicy @Inject constructor() {
    fun isCacheable(url: String): Boolean = url.contains("/products") || url.contains("/categories")
    fun getTtl(url: String): Long = if (url.contains("/products")) 300_000 else 60_000
}

@SingleIn(AppScope::class)
class TokenProvider @Inject constructor() {
    fun getAccessToken(): String = "mock_access_token"
    fun getRefreshToken(): String = "mock_refresh_token"
    fun isExpired(): Boolean = false
}

@SingleIn(AppScope::class)
class WebSocketManager @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {
    fun connect(channel: String) {}
    fun disconnect() {}
    fun send(message: String) {}
}

@SingleIn(AppScope::class)
class GraphQLClient @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) {
    fun query(query: String): String = "{}"
    fun mutate(mutation: String): String = "{}"
}

@SingleIn(AppScope::class)
class FileUploader @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {
    fun upload(filePath: String, endpoint: String): String = "upload_id_123"
}
