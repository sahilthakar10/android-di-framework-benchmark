package com.codeint.shopapp.koin.benchmark

import com.codeint.shopapp.common.platform.nanoTime
import com.codeint.shopapp.koin.di.*
import com.codeint.shopapp.koin.feature.home.HomeViewModel
import com.codeint.shopapp.koin.feature.search.SearchViewModel
import com.codeint.shopapp.koin.feature.productdetail.ProductDetailViewModel
import com.codeint.shopapp.koin.feature.cart.CartViewModel
import com.codeint.shopapp.koin.feature.checkout.CheckoutViewModel
import com.codeint.shopapp.koin.feature.profile.ProfileViewModel
import com.codeint.shopapp.koin.feature.chat.ChatViewModel
import com.codeint.shopapp.koin.feature.orders.OrderHistoryViewModel
import com.codeint.shopapp.koin.feature.settings.SettingsViewModel
import com.codeint.shopapp.koin.feature.notifications.NotificationsViewModel
import com.codeint.shopapp.koin.feature.onboarding.OnboardingViewModel
import com.codeint.shopapp.koin.feature.reviews.ReviewsViewModel
import com.codeint.shopapp.koin.feature.wishlist.WishlistViewModel
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.analytics.CrashReporter
import com.codeint.shopapp.koin.core.network.HttpClient
import com.codeint.shopapp.koin.core.network.AuthInterceptor
import com.codeint.shopapp.koin.core.network.RateLimiter
import com.codeint.shopapp.koin.core.network.WebSocketManager
import com.codeint.shopapp.koin.core.network.GraphQLClient
import com.codeint.shopapp.koin.core.network.TokenProvider
import com.codeint.shopapp.koin.core.network.NetworkLogger
import com.codeint.shopapp.koin.core.network.CachePolicy
import com.codeint.shopapp.koin.core.auth.AuthManager
import com.codeint.shopapp.koin.core.auth.TokenStorage
import com.codeint.shopapp.koin.core.auth.SessionManager
import com.codeint.shopapp.koin.core.storage.DatabaseManager
import com.codeint.shopapp.koin.core.storage.PreferencesManager
import com.codeint.shopapp.koin.core.storage.SecureStorage
import com.codeint.shopapp.koin.core.storage.CacheManager
import com.codeint.shopapp.koin.core.logging.AppLogger
import com.codeint.shopapp.koin.core.logging.AuditLogger
import com.codeint.shopapp.koin.core.image.ImageLoader
import com.codeint.shopapp.koin.core.config.FeatureFlagManager
import com.codeint.shopapp.koin.core.config.AppConfigProvider
import com.codeint.shopapp.koin.core.notification.NotificationManager
import com.codeint.shopapp.koin.core.notification.DeepLinkHandler
import com.codeint.shopapp.koin.core.location.LocationManager
import com.codeint.shopapp.koin.core.location.StoreLocator
import com.codeint.shopapp.koin.data.product.ProductRepository
import com.codeint.shopapp.koin.data.product.remote.ProductRemoteDataSource
import com.codeint.shopapp.koin.data.product.local.ProductLocalDataSource
import com.codeint.shopapp.koin.data.product.mapper.ProductMapper
import com.codeint.shopapp.koin.data.user.UserRepository
import com.codeint.shopapp.koin.data.user.remote.UserRemoteDataSource
import com.codeint.shopapp.koin.data.user.local.UserLocalDataSource
import com.codeint.shopapp.koin.data.user.mapper.UserMapper
import com.codeint.shopapp.koin.data.cart.CartRepository
import com.codeint.shopapp.koin.data.cart.remote.CartRemoteDataSource
import com.codeint.shopapp.koin.data.cart.local.CartLocalDataSource
import com.codeint.shopapp.koin.data.cart.mapper.CartMapper
import com.codeint.shopapp.koin.data.order.OrderRepository
import com.codeint.shopapp.koin.data.order.remote.OrderRemoteDataSource
import com.codeint.shopapp.koin.data.order.local.OrderLocalDataSource
import com.codeint.shopapp.koin.data.order.mapper.OrderMapper
import com.codeint.shopapp.koin.data.payment.PaymentRepository
import com.codeint.shopapp.koin.data.payment.remote.PaymentRemoteDataSource
import com.codeint.shopapp.koin.data.payment.local.PaymentLocalDataSource
import com.codeint.shopapp.koin.data.payment.mapper.PaymentMapper
import com.codeint.shopapp.koin.data.chat.ChatRepository
import com.codeint.shopapp.koin.data.chat.remote.ChatRemoteDataSource
import com.codeint.shopapp.koin.data.chat.local.ChatLocalDataSource
import com.codeint.shopapp.koin.data.chat.mapper.ChatMapper
import com.codeint.shopapp.koin.data.search.SearchRepository
import com.codeint.shopapp.koin.data.search.remote.SearchRemoteDataSource
import com.codeint.shopapp.koin.data.search.local.SearchLocalDataSource
import com.codeint.shopapp.koin.data.search.mapper.SearchMapper
import com.codeint.shopapp.koin.data.review.ReviewRepository
import com.codeint.shopapp.koin.data.review.remote.ReviewRemoteDataSource
import com.codeint.shopapp.koin.data.review.local.ReviewLocalDataSource
import com.codeint.shopapp.koin.data.review.mapper.ReviewMapper
import com.codeint.shopapp.koin.data.category.CategoryRepository
import com.codeint.shopapp.koin.data.category.remote.CategoryRemoteDataSource
import com.codeint.shopapp.koin.data.category.local.CategoryLocalDataSource
import com.codeint.shopapp.koin.data.category.mapper.CategoryMapper
import com.codeint.shopapp.koin.data.address.AddressRepository
import com.codeint.shopapp.koin.data.address.remote.AddressRemoteDataSource
import com.codeint.shopapp.koin.data.address.local.AddressLocalDataSource
import com.codeint.shopapp.koin.data.address.mapper.AddressMapper
import com.codeint.shopapp.koin.data.wishlist.WishlistRepository
import com.codeint.shopapp.koin.data.wishlist.remote.WishlistRemoteDataSource
import com.codeint.shopapp.koin.data.wishlist.local.WishlistLocalDataSource
import com.codeint.shopapp.koin.data.wishlist.mapper.WishlistMapper
import com.codeint.shopapp.koin.data.promotion.PromotionRepository
import com.codeint.shopapp.koin.data.promotion.remote.PromotionRemoteDataSource
import com.codeint.shopapp.koin.data.promotion.local.PromotionLocalDataSource
import com.codeint.shopapp.koin.data.promotion.mapper.PromotionMapper
import com.codeint.shopapp.koin.data.shipping.ShippingRepository
import com.codeint.shopapp.koin.data.shipping.remote.ShippingRemoteDataSource
import com.codeint.shopapp.koin.data.shipping.local.ShippingLocalDataSource
import com.codeint.shopapp.koin.data.shipping.mapper.ShippingMapper
import com.codeint.shopapp.koin.data.feed.FeedRepository
import com.codeint.shopapp.koin.data.feed.remote.FeedRemoteDataSource
import com.codeint.shopapp.koin.data.feed.local.FeedLocalDataSource
import com.codeint.shopapp.koin.data.feed.mapper.FeedMapper
import com.codeint.shopapp.koin.domain.product.GetProductListUseCase
import com.codeint.shopapp.koin.domain.product.GetProductDetailUseCase
import com.codeint.shopapp.koin.domain.user.GetUserListUseCase
import com.codeint.shopapp.koin.domain.user.GetUserDetailUseCase
import com.codeint.shopapp.koin.domain.cart.GetCartListUseCase
import com.codeint.shopapp.koin.domain.cart.GetCartDetailUseCase
import com.codeint.shopapp.koin.domain.order.GetOrderListUseCase
import com.codeint.shopapp.koin.domain.order.GetOrderDetailUseCase
import com.codeint.shopapp.koin.domain.payment.GetPaymentListUseCase
import com.codeint.shopapp.koin.domain.payment.GetPaymentDetailUseCase
import com.codeint.shopapp.koin.domain.chat.GetChatListUseCase
import com.codeint.shopapp.koin.domain.chat.GetChatDetailUseCase
import com.codeint.shopapp.koin.domain.search.GetSearchListUseCase
import com.codeint.shopapp.koin.domain.search.GetSearchDetailUseCase
import com.codeint.shopapp.koin.domain.review.GetReviewListUseCase
import com.codeint.shopapp.koin.domain.review.GetReviewDetailUseCase
import com.codeint.shopapp.koin.domain.category.GetCategoryListUseCase
import com.codeint.shopapp.koin.domain.category.GetCategoryDetailUseCase
import com.codeint.shopapp.koin.domain.address.GetAddressListUseCase
import com.codeint.shopapp.koin.domain.address.GetAddressDetailUseCase
import com.codeint.shopapp.koin.domain.wishlist.GetWishlistListUseCase
import com.codeint.shopapp.koin.domain.wishlist.GetWishlistDetailUseCase
import com.codeint.shopapp.koin.domain.promotion.GetPromotionListUseCase
import com.codeint.shopapp.koin.domain.promotion.GetPromotionDetailUseCase
import com.codeint.shopapp.koin.domain.shipping.GetShippingListUseCase
import com.codeint.shopapp.koin.domain.shipping.GetShippingDetailUseCase
import com.codeint.shopapp.koin.domain.feed.GetFeedListUseCase
import com.codeint.shopapp.koin.domain.feed.GetFeedDetailUseCase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

/**
 * Koin Runtime Benchmark — Proper Methodology
 *
 * Follows kotlinx-benchmark / JMH best practices:
 * 1. Warmup phase: startKoin + full resolution once (excluded from results)
 * 2. Single Koin lifecycle: startKoin() once, keep alive for all measurements
 *    (This matches real-world usage — Koin starts once at app launch and stays
 *    alive for the entire app session. Apps should NEVER cycle startKoin/stopKoin.)
 * 3. Blackhole: Consume resolved values to prevent dead code elimination
 * 4. createdAtStart: 19 critical singletons eagerly initialized (best practice)
 *
 * Reference: https://insert-koin.io/docs/reference/koin-mp/kmp/
 * Reference: https://github.com/Kotlin/kotlinx-benchmark/blob/master/docs/writing-benchmarks.md
 */

data class KoinBenchmarkResult(
    val initTimeNanos: Long,
    val coldInjectionNanos: Map<String, Long>,
    val warmInjectionAvgNanos: Map<String, Long>,
    val totalWarmNanos: Long
)

fun runKoinBenchmark(warmIterations: Int = 100): KoinBenchmarkResult {
    // ── SETUP: Clean any stale Koin state from previous runs ──
    try { stopKoin() } catch (_: Exception) {}

    // ── SETUP: Warmup phase (excluded from measurement) ──
    // Purpose: Warm up class loading, Koin's internal HashMap allocation,
    // and Kotlin/Native memory allocator.
    // startKoin once, resolve all 10 targets, then stopKoin.
    val warmupKoin = startKoin { modules(allShopAppModules) }.koin
    val warmupResolvers: List<() -> Any> = listOf(
        { warmupKoin.get<HomeViewModel>() },
        { warmupKoin.get<SearchViewModel>() },
        { warmupKoin.get<ProductDetailViewModel>() },
        { warmupKoin.get<CartViewModel>() },
        { warmupKoin.get<CheckoutViewModel>() },
        { warmupKoin.get<ProfileViewModel>() },
        { warmupKoin.get<ChatViewModel>() },
        { warmupKoin.get<OrderHistoryViewModel>() },
        { warmupKoin.get<AnalyticsTracker>() },
        { warmupKoin.get<ProductRepository>() }
    )
    repeat(5) {
        warmupResolvers.forEach { resolve -> blackhole(resolve()) }
    }
    stopKoin()

    // ── MEASUREMENT 1: Container initialization ──
    // Real-world scenario: This happens once at app startup.
    // Includes: module DSL processing, HashMap allocation, 19 createdAtStart singletons.
    val initStart = nanoTime()
    val koinApp = startKoin { modules(allShopAppModules) }
    val initTime = nanoTime() - initStart
    val koin = koinApp.koin

    // ── MEASUREMENT 2: Cold injection (first access from this Koin instance) ──
    // Real-world scenario: First screen load — singletons resolved for first time,
    // factory instances created with full dependency chain resolution.
    val cold = linkedMapOf<String, Long>()
    cold["HomeViewModel"] = measureNanos { blackhole(koin.get<HomeViewModel>()) }
    cold["SearchViewModel"] = measureNanos { blackhole(koin.get<SearchViewModel>()) }
    cold["ProductDetailVM"] = measureNanos { blackhole(koin.get<ProductDetailViewModel>()) }
    cold["CartViewModel"] = measureNanos { blackhole(koin.get<CartViewModel>()) }
    cold["CheckoutViewModel"] = measureNanos { blackhole(koin.get<CheckoutViewModel>()) }
    cold["ProfileViewModel"] = measureNanos { blackhole(koin.get<ProfileViewModel>()) }
    cold["ChatViewModel"] = measureNanos { blackhole(koin.get<ChatViewModel>()) }
    cold["OrderHistoryVM"] = measureNanos { blackhole(koin.get<OrderHistoryViewModel>()) }
    cold["AnalyticsTracker"] = measureNanos { blackhole(koin.get<AnalyticsTracker>()) }
    cold["ProductRepository"] = measureNanos { blackhole(koin.get<ProductRepository>()) }

    // ── MEASUREMENT 3: Warm injection (repeated access, same Koin instance) ──
    // Real-world scenario: User navigating between screens throughout the session.
    // Singletons return cached instances (HashMap lookup).
    // Factories create new instances but resolve singleton deps from cache.
    val warm = linkedMapOf<String, Long>()
    var totalWarm = 0L
    val targets = listOf<Pair<String, () -> Any>>(
        "HomeViewModel" to { koin.get<HomeViewModel>() },
        "SearchViewModel" to { koin.get<SearchViewModel>() },
        "ProductDetailVM" to { koin.get<ProductDetailViewModel>() },
        "CartViewModel" to { koin.get<CartViewModel>() },
        "CheckoutViewModel" to { koin.get<CheckoutViewModel>() },
        "ProfileViewModel" to { koin.get<ProfileViewModel>() },
        "ChatViewModel" to { koin.get<ChatViewModel>() },
        "OrderHistoryVM" to { koin.get<OrderHistoryViewModel>() },
        "AnalyticsTracker" to { koin.get<AnalyticsTracker>() },
        "ProductRepository" to { koin.get<ProductRepository>() }
    )
    for ((name, resolver) in targets) {
        var sum = 0L
        repeat(warmIterations) {
            val s = nanoTime()
            blackhole(resolver())
            sum += nanoTime() - s
        }
        warm[name] = sum / warmIterations
        totalWarm += sum
    }

    // ── TEARDOWN: Clean up for next benchmark run ──
    // stopKoin releases all singletons and clears the registry.
    stopKoin()
    return KoinBenchmarkResult(initTime, cold, warm, totalWarm)
}

// ── Full Benchmark (layered results for iOS detail screen) ──

data class LayerBenchmarkResult(
    val name: String,
    val count: Int,
    val description: String,
    val items: List<Pair<String, Long>>  // name to nanos
)

data class FullBenchmarkResult(
    val layers: List<LayerBenchmarkResult>
)

fun runFullBenchmark(): FullBenchmarkResult {
    // Clean any stale Koin state
    try { stopKoin() } catch (_: Exception) {}

    // Warmup
    val warmupKoin = startKoin { modules(allShopAppModules) }.koin
    blackhole(warmupKoin.get<HomeViewModel>())
    blackhole(warmupKoin.get<ProductRepository>())
    stopKoin()

    val layers = mutableListOf<LayerBenchmarkResult>()

    // Layer 1: Container Creation
    val graphItems = mutableListOf<Pair<String, Long>>()
    val initStart = nanoTime()
    val koinApp = startKoin { modules(allShopAppModules) }
    graphItems.add("startKoin()" to (nanoTime() - initStart))
    val koin = koinApp.koin
    layers.add(LayerBenchmarkResult("Graph Creation", 1, "DI container initialization", graphItems))

    // Layer 2: ViewModels (13)
    val vmItems = mutableListOf<Pair<String, Long>>()
    vmItems.add("HomeViewModel" to measureNanos { blackhole(koin.get<HomeViewModel>()) })
    vmItems.add("SearchViewModel" to measureNanos { blackhole(koin.get<SearchViewModel>()) })
    vmItems.add("ProductDetailViewModel" to measureNanos { blackhole(koin.get<ProductDetailViewModel>()) })
    vmItems.add("CartViewModel" to measureNanos { blackhole(koin.get<CartViewModel>()) })
    vmItems.add("CheckoutViewModel" to measureNanos { blackhole(koin.get<CheckoutViewModel>()) })
    vmItems.add("ProfileViewModel" to measureNanos { blackhole(koin.get<ProfileViewModel>()) })
    vmItems.add("ChatViewModel" to measureNanos { blackhole(koin.get<ChatViewModel>()) })
    vmItems.add("OrderHistoryViewModel" to measureNanos { blackhole(koin.get<OrderHistoryViewModel>()) })
    vmItems.add("SettingsViewModel" to measureNanos { blackhole(koin.get<SettingsViewModel>()) })
    vmItems.add("NotificationsViewModel" to measureNanos { blackhole(koin.get<NotificationsViewModel>()) })
    vmItems.add("OnboardingViewModel" to measureNanos { blackhole(koin.get<OnboardingViewModel>()) })
    vmItems.add("ReviewsViewModel" to measureNanos { blackhole(koin.get<ReviewsViewModel>()) })
    vmItems.add("WishlistViewModel" to measureNanos { blackhole(koin.get<WishlistViewModel>()) })
    layers.add(LayerBenchmarkResult("ViewModels", 13, "Feature-level presentation layer", vmItems))

    // Layer 3: Core Singletons (14)
    val coreItems = mutableListOf<Pair<String, Long>>()
    coreItems.add("HttpClient" to measureNanos { blackhole(koin.get<HttpClient>()) })
    coreItems.add("AuthManager" to measureNanos { blackhole(koin.get<AuthManager>()) })
    coreItems.add("TokenStorage" to measureNanos { blackhole(koin.get<TokenStorage>()) })
    coreItems.add("SessionManager" to measureNanos { blackhole(koin.get<SessionManager>()) })
    coreItems.add("AnalyticsTracker" to measureNanos { blackhole(koin.get<AnalyticsTracker>()) })
    coreItems.add("CrashReporter" to measureNanos { blackhole(koin.get<CrashReporter>()) })
    coreItems.add("DatabaseManager" to measureNanos { blackhole(koin.get<DatabaseManager>()) })
    coreItems.add("PreferencesManager" to measureNanos { blackhole(koin.get<PreferencesManager>()) })
    coreItems.add("SecureStorage" to measureNanos { blackhole(koin.get<SecureStorage>()) })
    coreItems.add("CacheManager" to measureNanos { blackhole(koin.get<CacheManager>()) })
    coreItems.add("AppLogger" to measureNanos { blackhole(koin.get<AppLogger>()) })
    coreItems.add("TokenProvider" to measureNanos { blackhole(koin.get<TokenProvider>()) })
    coreItems.add("NetworkLogger" to measureNanos { blackhole(koin.get<NetworkLogger>()) })
    coreItems.add("CachePolicy" to measureNanos { blackhole(koin.get<CachePolicy>()) })
    layers.add(LayerBenchmarkResult("Core Singletons", 14, "Shared infrastructure services", coreItems))

    // Layer 4: Core Services (12)
    val svcItems = mutableListOf<Pair<String, Long>>()
    svcItems.add("AuthInterceptor" to measureNanos { blackhole(koin.get<AuthInterceptor>()) })
    svcItems.add("RateLimiter" to measureNanos { blackhole(koin.get<RateLimiter>()) })
    svcItems.add("WebSocketManager" to measureNanos { blackhole(koin.get<WebSocketManager>()) })
    svcItems.add("GraphQLClient" to measureNanos { blackhole(koin.get<GraphQLClient>()) })
    svcItems.add("ImageLoader" to measureNanos { blackhole(koin.get<ImageLoader>()) })
    svcItems.add("FeatureFlagManager" to measureNanos { blackhole(koin.get<FeatureFlagManager>()) })
    svcItems.add("AppConfigProvider" to measureNanos { blackhole(koin.get<AppConfigProvider>()) })
    svcItems.add("NotificationManager" to measureNanos { blackhole(koin.get<NotificationManager>()) })
    svcItems.add("DeepLinkHandler" to measureNanos { blackhole(koin.get<DeepLinkHandler>()) })
    svcItems.add("LocationManager" to measureNanos { blackhole(koin.get<LocationManager>()) })
    svcItems.add("StoreLocator" to measureNanos { blackhole(koin.get<StoreLocator>()) })
    svcItems.add("AuditLogger" to measureNanos { blackhole(koin.get<AuditLogger>()) })
    layers.add(LayerBenchmarkResult("Core Services", 12, "Application-level services", svcItems))

    // Layer 5: Repositories (14)
    val repoItems = mutableListOf<Pair<String, Long>>()
    repoItems.add("ProductRepository" to measureNanos { blackhole(koin.get<ProductRepository>()) })
    repoItems.add("UserRepository" to measureNanos { blackhole(koin.get<UserRepository>()) })
    repoItems.add("CartRepository" to measureNanos { blackhole(koin.get<CartRepository>()) })
    repoItems.add("OrderRepository" to measureNanos { blackhole(koin.get<OrderRepository>()) })
    repoItems.add("PaymentRepository" to measureNanos { blackhole(koin.get<PaymentRepository>()) })
    repoItems.add("ChatRepository" to measureNanos { blackhole(koin.get<ChatRepository>()) })
    repoItems.add("SearchRepository" to measureNanos { blackhole(koin.get<SearchRepository>()) })
    repoItems.add("ReviewRepository" to measureNanos { blackhole(koin.get<ReviewRepository>()) })
    repoItems.add("CategoryRepository" to measureNanos { blackhole(koin.get<CategoryRepository>()) })
    repoItems.add("AddressRepository" to measureNanos { blackhole(koin.get<AddressRepository>()) })
    repoItems.add("WishlistRepository" to measureNanos { blackhole(koin.get<WishlistRepository>()) })
    repoItems.add("PromotionRepository" to measureNanos { blackhole(koin.get<PromotionRepository>()) })
    repoItems.add("ShippingRepository" to measureNanos { blackhole(koin.get<ShippingRepository>()) })
    repoItems.add("FeedRepository" to measureNanos { blackhole(koin.get<FeedRepository>()) })
    layers.add(LayerBenchmarkResult("Repositories", 14, "Data access abstraction layer", repoItems))

    // Layer 6: RemoteDataSources (14)
    val remoteItems = mutableListOf<Pair<String, Long>>()
    remoteItems.add("ProductRemoteDataSource" to measureNanos { blackhole(koin.get<ProductRemoteDataSource>()) })
    remoteItems.add("UserRemoteDataSource" to measureNanos { blackhole(koin.get<UserRemoteDataSource>()) })
    remoteItems.add("CartRemoteDataSource" to measureNanos { blackhole(koin.get<CartRemoteDataSource>()) })
    remoteItems.add("OrderRemoteDataSource" to measureNanos { blackhole(koin.get<OrderRemoteDataSource>()) })
    remoteItems.add("PaymentRemoteDataSource" to measureNanos { blackhole(koin.get<PaymentRemoteDataSource>()) })
    remoteItems.add("ChatRemoteDataSource" to measureNanos { blackhole(koin.get<ChatRemoteDataSource>()) })
    remoteItems.add("SearchRemoteDataSource" to measureNanos { blackhole(koin.get<SearchRemoteDataSource>()) })
    remoteItems.add("ReviewRemoteDataSource" to measureNanos { blackhole(koin.get<ReviewRemoteDataSource>()) })
    remoteItems.add("CategoryRemoteDataSource" to measureNanos { blackhole(koin.get<CategoryRemoteDataSource>()) })
    remoteItems.add("AddressRemoteDataSource" to measureNanos { blackhole(koin.get<AddressRemoteDataSource>()) })
    remoteItems.add("WishlistRemoteDataSource" to measureNanos { blackhole(koin.get<WishlistRemoteDataSource>()) })
    remoteItems.add("PromotionRemoteDataSource" to measureNanos { blackhole(koin.get<PromotionRemoteDataSource>()) })
    remoteItems.add("ShippingRemoteDataSource" to measureNanos { blackhole(koin.get<ShippingRemoteDataSource>()) })
    remoteItems.add("FeedRemoteDataSource" to measureNanos { blackhole(koin.get<FeedRemoteDataSource>()) })
    layers.add(LayerBenchmarkResult("RemoteDataSources", 14, "Network API data sources", remoteItems))

    // Layer 7: LocalDataSources (14)
    val localItems = mutableListOf<Pair<String, Long>>()
    localItems.add("ProductLocalDataSource" to measureNanos { blackhole(koin.get<ProductLocalDataSource>()) })
    localItems.add("UserLocalDataSource" to measureNanos { blackhole(koin.get<UserLocalDataSource>()) })
    localItems.add("CartLocalDataSource" to measureNanos { blackhole(koin.get<CartLocalDataSource>()) })
    localItems.add("OrderLocalDataSource" to measureNanos { blackhole(koin.get<OrderLocalDataSource>()) })
    localItems.add("PaymentLocalDataSource" to measureNanos { blackhole(koin.get<PaymentLocalDataSource>()) })
    localItems.add("ChatLocalDataSource" to measureNanos { blackhole(koin.get<ChatLocalDataSource>()) })
    localItems.add("SearchLocalDataSource" to measureNanos { blackhole(koin.get<SearchLocalDataSource>()) })
    localItems.add("ReviewLocalDataSource" to measureNanos { blackhole(koin.get<ReviewLocalDataSource>()) })
    localItems.add("CategoryLocalDataSource" to measureNanos { blackhole(koin.get<CategoryLocalDataSource>()) })
    localItems.add("AddressLocalDataSource" to measureNanos { blackhole(koin.get<AddressLocalDataSource>()) })
    localItems.add("WishlistLocalDataSource" to measureNanos { blackhole(koin.get<WishlistLocalDataSource>()) })
    localItems.add("PromotionLocalDataSource" to measureNanos { blackhole(koin.get<PromotionLocalDataSource>()) })
    localItems.add("ShippingLocalDataSource" to measureNanos { blackhole(koin.get<ShippingLocalDataSource>()) })
    localItems.add("FeedLocalDataSource" to measureNanos { blackhole(koin.get<FeedLocalDataSource>()) })
    layers.add(LayerBenchmarkResult("LocalDataSources", 14, "Local/cached data sources", localItems))

    // Layer 8: Mappers (14)
    val mapperItems = mutableListOf<Pair<String, Long>>()
    mapperItems.add("ProductMapper" to measureNanos { blackhole(koin.get<ProductMapper>()) })
    mapperItems.add("UserMapper" to measureNanos { blackhole(koin.get<UserMapper>()) })
    mapperItems.add("CartMapper" to measureNanos { blackhole(koin.get<CartMapper>()) })
    mapperItems.add("OrderMapper" to measureNanos { blackhole(koin.get<OrderMapper>()) })
    mapperItems.add("PaymentMapper" to measureNanos { blackhole(koin.get<PaymentMapper>()) })
    mapperItems.add("ChatMapper" to measureNanos { blackhole(koin.get<ChatMapper>()) })
    mapperItems.add("SearchMapper" to measureNanos { blackhole(koin.get<SearchMapper>()) })
    mapperItems.add("ReviewMapper" to measureNanos { blackhole(koin.get<ReviewMapper>()) })
    mapperItems.add("CategoryMapper" to measureNanos { blackhole(koin.get<CategoryMapper>()) })
    mapperItems.add("AddressMapper" to measureNanos { blackhole(koin.get<AddressMapper>()) })
    mapperItems.add("WishlistMapper" to measureNanos { blackhole(koin.get<WishlistMapper>()) })
    mapperItems.add("PromotionMapper" to measureNanos { blackhole(koin.get<PromotionMapper>()) })
    mapperItems.add("ShippingMapper" to measureNanos { blackhole(koin.get<ShippingMapper>()) })
    mapperItems.add("FeedMapper" to measureNanos { blackhole(koin.get<FeedMapper>()) })
    layers.add(LayerBenchmarkResult("Mappers", 14, "Data transformation layer", mapperItems))

    // Layer 9: UseCases (28)
    val ucItems = mutableListOf<Pair<String, Long>>()
    ucItems.add("GetProductListUseCase" to measureNanos { blackhole(koin.get<GetProductListUseCase>()) })
    ucItems.add("GetProductDetailUseCase" to measureNanos { blackhole(koin.get<GetProductDetailUseCase>()) })
    ucItems.add("GetUserListUseCase" to measureNanos { blackhole(koin.get<GetUserListUseCase>()) })
    ucItems.add("GetUserDetailUseCase" to measureNanos { blackhole(koin.get<GetUserDetailUseCase>()) })
    ucItems.add("GetCartListUseCase" to measureNanos { blackhole(koin.get<GetCartListUseCase>()) })
    ucItems.add("GetCartDetailUseCase" to measureNanos { blackhole(koin.get<GetCartDetailUseCase>()) })
    ucItems.add("GetOrderListUseCase" to measureNanos { blackhole(koin.get<GetOrderListUseCase>()) })
    ucItems.add("GetOrderDetailUseCase" to measureNanos { blackhole(koin.get<GetOrderDetailUseCase>()) })
    ucItems.add("GetPaymentListUseCase" to measureNanos { blackhole(koin.get<GetPaymentListUseCase>()) })
    ucItems.add("GetPaymentDetailUseCase" to measureNanos { blackhole(koin.get<GetPaymentDetailUseCase>()) })
    ucItems.add("GetChatListUseCase" to measureNanos { blackhole(koin.get<GetChatListUseCase>()) })
    ucItems.add("GetChatDetailUseCase" to measureNanos { blackhole(koin.get<GetChatDetailUseCase>()) })
    ucItems.add("GetSearchListUseCase" to measureNanos { blackhole(koin.get<GetSearchListUseCase>()) })
    ucItems.add("GetSearchDetailUseCase" to measureNanos { blackhole(koin.get<GetSearchDetailUseCase>()) })
    ucItems.add("GetReviewListUseCase" to measureNanos { blackhole(koin.get<GetReviewListUseCase>()) })
    ucItems.add("GetReviewDetailUseCase" to measureNanos { blackhole(koin.get<GetReviewDetailUseCase>()) })
    ucItems.add("GetCategoryListUseCase" to measureNanos { blackhole(koin.get<GetCategoryListUseCase>()) })
    ucItems.add("GetCategoryDetailUseCase" to measureNanos { blackhole(koin.get<GetCategoryDetailUseCase>()) })
    ucItems.add("GetAddressListUseCase" to measureNanos { blackhole(koin.get<GetAddressListUseCase>()) })
    ucItems.add("GetAddressDetailUseCase" to measureNanos { blackhole(koin.get<GetAddressDetailUseCase>()) })
    ucItems.add("GetWishlistListUseCase" to measureNanos { blackhole(koin.get<GetWishlistListUseCase>()) })
    ucItems.add("GetWishlistDetailUseCase" to measureNanos { blackhole(koin.get<GetWishlistDetailUseCase>()) })
    ucItems.add("GetPromotionListUseCase" to measureNanos { blackhole(koin.get<GetPromotionListUseCase>()) })
    ucItems.add("GetPromotionDetailUseCase" to measureNanos { blackhole(koin.get<GetPromotionDetailUseCase>()) })
    ucItems.add("GetShippingListUseCase" to measureNanos { blackhole(koin.get<GetShippingListUseCase>()) })
    ucItems.add("GetShippingDetailUseCase" to measureNanos { blackhole(koin.get<GetShippingDetailUseCase>()) })
    ucItems.add("GetFeedListUseCase" to measureNanos { blackhole(koin.get<GetFeedListUseCase>()) })
    ucItems.add("GetFeedDetailUseCase" to measureNanos { blackhole(koin.get<GetFeedDetailUseCase>()) })
    layers.add(LayerBenchmarkResult("UseCases", 28, "Business logic / domain layer", ucItems))

    stopKoin()
    return FullBenchmarkResult(layers)
}

// Blackhole: prevents LLVM dead code elimination on Kotlin/Native.
@Suppress("UNUSED_PARAMETER")
private fun blackhole(value: Any?) {
    // Intentionally empty — the function call itself prevents DCE
}

private inline fun measureNanos(block: () -> Unit): Long {
    val s = nanoTime()
    block()
    return nanoTime() - s
}
