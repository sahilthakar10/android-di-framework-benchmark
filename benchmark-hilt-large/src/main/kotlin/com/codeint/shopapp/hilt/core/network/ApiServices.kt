package com.codeint.shopapp.hilt.core.network

import javax.inject.Inject

// ── Interfaces ──

interface TokenProvider {
    fun getAccessToken(): String
    fun getRefreshToken(): String
    fun isExpired(): Boolean
}

interface NetworkLogger {
    fun logRequest(method: String, url: String, status: Int)
    fun logError(url: String, error: String)
}

interface CachePolicy {
    fun isCacheable(url: String): Boolean
    fun getTtl(url: String): Long
}

// ── Implementations ──

class RealTokenProvider @Inject constructor() : TokenProvider {
    override fun getAccessToken() = "mock_access_token"
    override fun getRefreshToken() = "mock_refresh_token"
    override fun isExpired() = false
}

class RealNetworkLogger @Inject constructor() : NetworkLogger {
    override fun logRequest(method: String, url: String, status: Int) {}
    override fun logError(url: String, error: String) {}
}

class RealCachePolicy @Inject constructor() : CachePolicy {
    override fun isCacheable(url: String) = url.contains("/products") || url.contains("/categories")
    override fun getTtl(url: String) = if (url.contains("/products")) 300_000L else 60_000L
}

class AuthInterceptor @Inject constructor(private val tokenProvider: TokenProvider) {
    fun intercept(url: String): Map<String, String> = mapOf("Authorization" to "Bearer ${tokenProvider.getAccessToken()}")
}

class CacheInterceptor @Inject constructor(private val cachePolicy: CachePolicy) {
    fun shouldCache(url: String) = cachePolicy.isCacheable(url)
}

class LoggingInterceptor @Inject constructor(private val networkLogger: NetworkLogger) {
    fun log(method: String, url: String, status: Int) { networkLogger.logRequest(method, url, status) }
}

class RateLimiter @Inject constructor(private val config: RetryPolicy) {
    private val requestCounts = mutableMapOf<String, Int>()
    fun shouldThrottle(endpoint: String) = (requestCounts[endpoint] ?: 0) > 100
}

class WebSocketManager @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {
    fun connect(channel: String) {}
    fun disconnect() {}
    fun send(message: String) {}
}

class GraphQLClient @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) {
    fun query(query: String): String = "{}"
    fun mutate(mutation: String): String = "{}"
}

class FileUploader @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {
    fun upload(filePath: String, endpoint: String): String = "upload_id_123"
}
