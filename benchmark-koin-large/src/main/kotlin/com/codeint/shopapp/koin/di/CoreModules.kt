package com.codeint.shopapp.koin.di

import org.koin.dsl.module
import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.core.auth.*
import com.codeint.shopapp.koin.core.analytics.*
import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.core.config.*
import com.codeint.shopapp.koin.core.logging.*
import com.codeint.shopapp.koin.core.image.*
import com.codeint.shopapp.koin.core.notification.*
import com.codeint.shopapp.koin.core.location.*

val coreNetworkModule = module {
    // Critical infrastructure - eagerly created at startKoin() to avoid
    // first-request latency. These are used by almost every data source.
    single(createdAtStart = true) { HttpClient("https://api.shopapp.com", 30_000) }
    single(createdAtStart = true) { ApiResponseParser() }
    single(createdAtStart = true) { NetworkMonitor() }
    single { RetryPolicy(3, 1000) }
    single { SslPinningConfig(listOf("sha256/abc123")) }
    single(createdAtStart = true) { TokenProvider() }
    single { NetworkLogger() }
    single { CachePolicy() }
    single(createdAtStart = true) { AuthInterceptor(get()) }
    single { CacheInterceptor(get()) }
    single { LoggingInterceptor(get()) }
    single { RateLimiter(get()) }
    single { WebSocketManager(get(), get()) }
    single { GraphQLClient(get(), get(), get()) }
    single { FileUploader(get(), get()) }
}

val coreStorageModule = module {
    // Database and preferences are used everywhere - eager init
    single(createdAtStart = true) { DatabaseManager() }
    single(createdAtStart = true) { PreferencesManager() }
    single(createdAtStart = true) { SecureStorage() }
    single(createdAtStart = true) { CacheManager(get()) }
    single { FileManager() }
    single { DownloadManager(get()) }
}

val coreAuthModule = module {
    single(createdAtStart = true) { TokenStorage(get()) }
    single(createdAtStart = true) { SessionManager(get()) }
    single(createdAtStart = true) { AuthManager(get(), get(), get()) }
    single { BiometricAuthProvider(get()) }
    single { OAuthManager(get(), get()) }
    single { PasswordValidator() }
    single { TwoFactorAuthManager(get(), get()) }
}

val coreAnalyticsModule = module {
    // Analytics must be ready before any screen loads
    single(createdAtStart = true) { EventBus() }
    single(createdAtStart = true) { UserPropertyTracker() }
    single(createdAtStart = true) { RemoteConfigManager() }
    single(createdAtStart = true) { CrashReporter() }
    single { PerformanceMonitor() }
    single(createdAtStart = true) { AnalyticsTracker(get(), get()) }
    single { ABTestManager(get()) }
    single { ConsentManager(get()) }
}

val coreConfigModule = module {
    single(createdAtStart = true) { FeatureFlagManager(get()) }
    single { AppConfigProvider(get()) }
    single { ThemeManager(get()) }
    single { LocaleManager(get()) }
    single { EnvironmentManager() }
}

val coreLoggingModule = module {
    single(createdAtStart = true) { AppLogger(get()) }
    single { AuditLogger(get()) }
}

val coreImageModule = module {
    single { ImageLoader(get(), get()) }
    single { ImageProcessor() }
    single { ThumbnailGenerator(get()) }
}

val coreNotificationModule = module {
    single { NotificationManager(get()) }
    single { PushTokenManager(get()) }
    single { DeepLinkHandler() }
    single { InAppMessageManager(get(), get()) }
}

val coreLocationModule = module {
    single { LocationManager(get()) }
    single { GeocodingService() }
    single { StoreLocator(get(), get()) }
}
