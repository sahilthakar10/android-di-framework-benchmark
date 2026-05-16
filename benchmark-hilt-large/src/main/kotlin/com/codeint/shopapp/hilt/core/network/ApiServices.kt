package com.codeint.shopapp.hilt.core.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton class AuthInterceptor @Inject constructor(private val tokenProvider: TokenProvider) {
    fun intercept(url: String): Map<String, String> = mapOf("Authorization" to "Bearer ${tokenProvider.getAccessToken()}")
}

@Singleton class CacheInterceptor @Inject constructor(private val cachePolicy: CachePolicy) {
    fun shouldCache(url: String): Boolean = cachePolicy.isCacheable(url)
}

@Singleton class LoggingInterceptor @Inject constructor(private val networkLogger: NetworkLogger) {
    fun log(method: String, url: String, status: Int) { networkLogger.logRequest(method, url, status) }
}

@Singleton class RateLimiter @Inject constructor(private val config: RetryPolicy) {
    private val requestCounts = mutableMapOf<String, Int>()
    fun shouldThrottle(endpoint: String): Boolean = (requestCounts[endpoint] ?: 0) > 100
}

@Singleton class NetworkLogger @Inject constructor() {
    fun logRequest(method: String, url: String, status: Int) {}
    fun logError(url: String, error: String) {}
}

@Singleton class CachePolicy @Inject constructor() {
    fun isCacheable(url: String): Boolean = url.contains("/products") || url.contains("/categories")
    fun getTtl(url: String): Long = if (url.contains("/products")) 300_000 else 60_000
}

@Singleton class TokenProvider @Inject constructor() {
    fun getAccessToken(): String = "mock_access_token"
    fun getRefreshToken(): String = "mock_refresh_token"
    fun isExpired(): Boolean = false
}

@Singleton class WebSocketManager @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {
    fun connect(channel: String) {}
    fun disconnect() {}
    fun send(message: String) {}
}

@Singleton class GraphQLClient @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor, private val parser: ApiResponseParser) {
    fun query(query: String): String = "{}"
    fun mutate(mutation: String): String = "{}"
}

@Singleton class FileUploader @Inject constructor(private val httpClient: HttpClient, private val authInterceptor: AuthInterceptor) {
    fun upload(filePath: String, endpoint: String): String = "upload_id_123"
}
