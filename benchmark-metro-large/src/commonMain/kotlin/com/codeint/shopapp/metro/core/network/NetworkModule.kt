package com.codeint.shopapp.metro.core.network

class HttpClient(val baseUrl: String, val timeout: Long)
class ApiResponseParser
class NetworkMonitor
class RetryPolicy(val maxRetries: Int, val backoffMs: Long)
class SslPinningConfig(val pins: List<String>)
