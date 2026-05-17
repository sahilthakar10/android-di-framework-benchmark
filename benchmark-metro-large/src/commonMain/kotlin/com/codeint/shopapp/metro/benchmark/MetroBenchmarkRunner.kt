package com.codeint.shopapp.metro.benchmark

import com.codeint.shopapp.metro.graph.GraphFactory

/**
 * Metro Runtime Benchmark — Proper Methodology
 *
 * Follows kotlinx-benchmark / JMH best practices:
 * 1. Warmup phase: Multiple iterations to stabilize (excluded from results)
 * 2. Blackhole: Return values consumed to prevent dead code elimination
 * 3. Single graph lifecycle: createGraph() once, measure injection patterns
 *    (This matches real-world usage — graph is created at app startup)
 * 4. Measurement: Multiple iterations averaged for statistical significance
 */

data class MetroBenchmarkResult(
    val initTimeNanos: Long,
    val coldInjectionNanos: Map<String, Long>,
    val warmInjectionAvgNanos: Map<String, Long>,
    val totalWarmNanos: Long
)

fun runMetroBenchmark(warmIterations: Int = 100): MetroBenchmarkResult {
    // ── SETUP: Warmup phase (excluded from measurement) ──
    val warmupGraph = GraphFactory.create()
    val warmupTargets = listOf(
        { warmupGraph.homeViewModel },
        { warmupGraph.searchViewModel },
        { warmupGraph.productDetailViewModel },
        { warmupGraph.cartViewModel },
        { warmupGraph.checkoutViewModel },
        { warmupGraph.profileViewModel },
        { warmupGraph.chatViewModel },
        { warmupGraph.orderHistoryViewModel },
        { warmupGraph.analyticsTracker },
        { warmupGraph.productRepository }
    )
    // Run warmup iterations — results discarded
    repeat(5) {
        warmupTargets.forEach { resolve -> blackhole(resolve()) }
    }

    // ── MEASUREMENT 1: Container initialization ──
    val initStart = nanoTime()
    val graph = GraphFactory.create()
    val initTime = nanoTime() - initStart

    // ── MEASUREMENT 2: Cold injection (first access after fresh graph) ──
    val cold = linkedMapOf<String, Long>()
    cold["HomeViewModel"] = measureNanos { blackhole(graph.homeViewModel) }
    cold["SearchViewModel"] = measureNanos { blackhole(graph.searchViewModel) }
    cold["ProductDetailVM"] = measureNanos { blackhole(graph.productDetailViewModel) }
    cold["CartViewModel"] = measureNanos { blackhole(graph.cartViewModel) }
    cold["CheckoutViewModel"] = measureNanos { blackhole(graph.checkoutViewModel) }
    cold["ProfileViewModel"] = measureNanos { blackhole(graph.profileViewModel) }
    cold["ChatViewModel"] = measureNanos { blackhole(graph.chatViewModel) }
    cold["OrderHistoryVM"] = measureNanos { blackhole(graph.orderHistoryViewModel) }
    cold["AnalyticsTracker"] = measureNanos { blackhole(graph.analyticsTracker) }
    cold["ProductRepository"] = measureNanos { blackhole(graph.productRepository) }

    // ── MEASUREMENT 3: Warm injection (repeated access) ──
    val warm = linkedMapOf<String, Long>()
    var totalWarm = 0L
    val targets = listOf<Pair<String, () -> Any>>(
        "HomeViewModel" to { graph.homeViewModel },
        "SearchViewModel" to { graph.searchViewModel },
        "ProductDetailVM" to { graph.productDetailViewModel },
        "CartViewModel" to { graph.cartViewModel },
        "CheckoutViewModel" to { graph.checkoutViewModel },
        "ProfileViewModel" to { graph.profileViewModel },
        "ChatViewModel" to { graph.chatViewModel },
        "OrderHistoryVM" to { graph.orderHistoryViewModel },
        "AnalyticsTracker" to { graph.analyticsTracker },
        "ProductRepository" to { graph.productRepository }
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

    return MetroBenchmarkResult(initTime, cold, warm, totalWarm)
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
    // Warmup
    val warmupGraph = GraphFactory.create()
    blackhole(warmupGraph.homeViewModel)
    blackhole(warmupGraph.productRepository)

    val layers = mutableListOf<LayerBenchmarkResult>()

    // Layer 1: Graph Creation
    val graphItems = mutableListOf<Pair<String, Long>>()
    val graphStart = nanoTime()
    val graph = GraphFactory.create()
    graphItems.add("GraphFactory.create()" to (nanoTime() - graphStart))
    layers.add(LayerBenchmarkResult("Graph Creation", 1, "DI container initialization", graphItems))

    // Layer 2: ViewModels (13)
    val vmItems = mutableListOf<Pair<String, Long>>()
    vmItems.add("HomeViewModel" to measureNanos { blackhole(graph.homeViewModel) })
    vmItems.add("SearchViewModel" to measureNanos { blackhole(graph.searchViewModel) })
    vmItems.add("ProductDetailViewModel" to measureNanos { blackhole(graph.productDetailViewModel) })
    vmItems.add("CartViewModel" to measureNanos { blackhole(graph.cartViewModel) })
    vmItems.add("CheckoutViewModel" to measureNanos { blackhole(graph.checkoutViewModel) })
    vmItems.add("ProfileViewModel" to measureNanos { blackhole(graph.profileViewModel) })
    vmItems.add("ChatViewModel" to measureNanos { blackhole(graph.chatViewModel) })
    vmItems.add("OrderHistoryViewModel" to measureNanos { blackhole(graph.orderHistoryViewModel) })
    vmItems.add("SettingsViewModel" to measureNanos { blackhole(graph.settingsViewModel) })
    vmItems.add("NotificationsViewModel" to measureNanos { blackhole(graph.notificationsViewModel) })
    vmItems.add("OnboardingViewModel" to measureNanos { blackhole(graph.onboardingViewModel) })
    vmItems.add("ReviewsViewModel" to measureNanos { blackhole(graph.reviewsViewModel) })
    vmItems.add("WishlistViewModel" to measureNanos { blackhole(graph.wishlistViewModel) })
    layers.add(LayerBenchmarkResult("ViewModels", 13, "Feature-level presentation layer", vmItems))

    // Layer 3: Core Singletons (14)
    val coreItems = mutableListOf<Pair<String, Long>>()
    coreItems.add("HttpClient" to measureNanos { blackhole(graph.httpClient) })
    coreItems.add("AuthManager" to measureNanos { blackhole(graph.authManager) })
    coreItems.add("TokenStorage" to measureNanos { blackhole(graph.tokenStorage) })
    coreItems.add("SessionManager" to measureNanos { blackhole(graph.sessionManager) })
    coreItems.add("AnalyticsTracker" to measureNanos { blackhole(graph.analyticsTracker) })
    coreItems.add("CrashReporter" to measureNanos { blackhole(graph.crashReporter) })
    coreItems.add("DatabaseManager" to measureNanos { blackhole(graph.databaseManager) })
    coreItems.add("PreferencesManager" to measureNanos { blackhole(graph.preferencesManager) })
    coreItems.add("SecureStorage" to measureNanos { blackhole(graph.secureStorage) })
    coreItems.add("CacheManager" to measureNanos { blackhole(graph.cacheManager) })
    coreItems.add("AppLogger" to measureNanos { blackhole(graph.appLogger) })
    coreItems.add("TokenProvider" to measureNanos { blackhole(graph.tokenProvider) })
    coreItems.add("NetworkLogger" to measureNanos { blackhole(graph.networkLogger) })
    coreItems.add("CachePolicy" to measureNanos { blackhole(graph.cachePolicy) })
    layers.add(LayerBenchmarkResult("Core Singletons", 14, "Shared infrastructure services", coreItems))

    // Layer 4: Core Services (12)
    val svcItems = mutableListOf<Pair<String, Long>>()
    svcItems.add("AuthInterceptor" to measureNanos { blackhole(graph.authInterceptor) })
    svcItems.add("RateLimiter" to measureNanos { blackhole(graph.rateLimiter) })
    svcItems.add("WebSocketManager" to measureNanos { blackhole(graph.webSocketManager) })
    svcItems.add("GraphQLClient" to measureNanos { blackhole(graph.graphQLClient) })
    svcItems.add("ImageLoader" to measureNanos { blackhole(graph.imageLoader) })
    svcItems.add("FeatureFlagManager" to measureNanos { blackhole(graph.featureFlagManager) })
    svcItems.add("AppConfigProvider" to measureNanos { blackhole(graph.appConfigProvider) })
    svcItems.add("NotificationManager" to measureNanos { blackhole(graph.notificationManager) })
    svcItems.add("DeepLinkHandler" to measureNanos { blackhole(graph.deepLinkHandler) })
    svcItems.add("LocationManager" to measureNanos { blackhole(graph.locationManager) })
    svcItems.add("StoreLocator" to measureNanos { blackhole(graph.storeLocator) })
    svcItems.add("AuditLogger" to measureNanos { blackhole(graph.auditLogger) })
    layers.add(LayerBenchmarkResult("Core Services", 12, "Application-level services", svcItems))

    // Layer 5: Repositories (14)
    val repoItems = mutableListOf<Pair<String, Long>>()
    repoItems.add("ProductRepository" to measureNanos { blackhole(graph.productRepository) })
    repoItems.add("UserRepository" to measureNanos { blackhole(graph.userRepository) })
    repoItems.add("CartRepository" to measureNanos { blackhole(graph.cartRepository) })
    repoItems.add("OrderRepository" to measureNanos { blackhole(graph.orderRepository) })
    repoItems.add("PaymentRepository" to measureNanos { blackhole(graph.paymentRepository) })
    repoItems.add("ChatRepository" to measureNanos { blackhole(graph.chatRepository) })
    repoItems.add("SearchRepository" to measureNanos { blackhole(graph.searchRepository) })
    repoItems.add("ReviewRepository" to measureNanos { blackhole(graph.reviewRepository) })
    repoItems.add("CategoryRepository" to measureNanos { blackhole(graph.categoryRepository) })
    repoItems.add("AddressRepository" to measureNanos { blackhole(graph.addressRepository) })
    repoItems.add("WishlistRepository" to measureNanos { blackhole(graph.wishlistRepository) })
    repoItems.add("PromotionRepository" to measureNanos { blackhole(graph.promotionRepository) })
    repoItems.add("ShippingRepository" to measureNanos { blackhole(graph.shippingRepository) })
    repoItems.add("FeedRepository" to measureNanos { blackhole(graph.feedRepository) })
    layers.add(LayerBenchmarkResult("Repositories", 14, "Data access abstraction layer", repoItems))

    // Layer 6: RemoteDataSources (14)
    val remoteItems = mutableListOf<Pair<String, Long>>()
    remoteItems.add("ProductRemoteDataSource" to measureNanos { blackhole(graph.productRemoteDataSource) })
    remoteItems.add("UserRemoteDataSource" to measureNanos { blackhole(graph.userRemoteDataSource) })
    remoteItems.add("CartRemoteDataSource" to measureNanos { blackhole(graph.cartRemoteDataSource) })
    remoteItems.add("OrderRemoteDataSource" to measureNanos { blackhole(graph.orderRemoteDataSource) })
    remoteItems.add("PaymentRemoteDataSource" to measureNanos { blackhole(graph.paymentRemoteDataSource) })
    remoteItems.add("ChatRemoteDataSource" to measureNanos { blackhole(graph.chatRemoteDataSource) })
    remoteItems.add("SearchRemoteDataSource" to measureNanos { blackhole(graph.searchRemoteDataSource) })
    remoteItems.add("ReviewRemoteDataSource" to measureNanos { blackhole(graph.reviewRemoteDataSource) })
    remoteItems.add("CategoryRemoteDataSource" to measureNanos { blackhole(graph.categoryRemoteDataSource) })
    remoteItems.add("AddressRemoteDataSource" to measureNanos { blackhole(graph.addressRemoteDataSource) })
    remoteItems.add("WishlistRemoteDataSource" to measureNanos { blackhole(graph.wishlistRemoteDataSource) })
    remoteItems.add("PromotionRemoteDataSource" to measureNanos { blackhole(graph.promotionRemoteDataSource) })
    remoteItems.add("ShippingRemoteDataSource" to measureNanos { blackhole(graph.shippingRemoteDataSource) })
    remoteItems.add("FeedRemoteDataSource" to measureNanos { blackhole(graph.feedRemoteDataSource) })
    layers.add(LayerBenchmarkResult("RemoteDataSources", 14, "Network API data sources", remoteItems))

    // Layer 7: LocalDataSources (14)
    val localItems = mutableListOf<Pair<String, Long>>()
    localItems.add("ProductLocalDataSource" to measureNanos { blackhole(graph.productLocalDataSource) })
    localItems.add("UserLocalDataSource" to measureNanos { blackhole(graph.userLocalDataSource) })
    localItems.add("CartLocalDataSource" to measureNanos { blackhole(graph.cartLocalDataSource) })
    localItems.add("OrderLocalDataSource" to measureNanos { blackhole(graph.orderLocalDataSource) })
    localItems.add("PaymentLocalDataSource" to measureNanos { blackhole(graph.paymentLocalDataSource) })
    localItems.add("ChatLocalDataSource" to measureNanos { blackhole(graph.chatLocalDataSource) })
    localItems.add("SearchLocalDataSource" to measureNanos { blackhole(graph.searchLocalDataSource) })
    localItems.add("ReviewLocalDataSource" to measureNanos { blackhole(graph.reviewLocalDataSource) })
    localItems.add("CategoryLocalDataSource" to measureNanos { blackhole(graph.categoryLocalDataSource) })
    localItems.add("AddressLocalDataSource" to measureNanos { blackhole(graph.addressLocalDataSource) })
    localItems.add("WishlistLocalDataSource" to measureNanos { blackhole(graph.wishlistLocalDataSource) })
    localItems.add("PromotionLocalDataSource" to measureNanos { blackhole(graph.promotionLocalDataSource) })
    localItems.add("ShippingLocalDataSource" to measureNanos { blackhole(graph.shippingLocalDataSource) })
    localItems.add("FeedLocalDataSource" to measureNanos { blackhole(graph.feedLocalDataSource) })
    layers.add(LayerBenchmarkResult("LocalDataSources", 14, "Local/cached data sources", localItems))

    // Layer 8: Mappers (14)
    val mapperItems = mutableListOf<Pair<String, Long>>()
    mapperItems.add("ProductMapper" to measureNanos { blackhole(graph.productMapper) })
    mapperItems.add("UserMapper" to measureNanos { blackhole(graph.userMapper) })
    mapperItems.add("CartMapper" to measureNanos { blackhole(graph.cartMapper) })
    mapperItems.add("OrderMapper" to measureNanos { blackhole(graph.orderMapper) })
    mapperItems.add("PaymentMapper" to measureNanos { blackhole(graph.paymentMapper) })
    mapperItems.add("ChatMapper" to measureNanos { blackhole(graph.chatMapper) })
    mapperItems.add("SearchMapper" to measureNanos { blackhole(graph.searchMapper) })
    mapperItems.add("ReviewMapper" to measureNanos { blackhole(graph.reviewMapper) })
    mapperItems.add("CategoryMapper" to measureNanos { blackhole(graph.categoryMapper) })
    mapperItems.add("AddressMapper" to measureNanos { blackhole(graph.addressMapper) })
    mapperItems.add("WishlistMapper" to measureNanos { blackhole(graph.wishlistMapper) })
    mapperItems.add("PromotionMapper" to measureNanos { blackhole(graph.promotionMapper) })
    mapperItems.add("ShippingMapper" to measureNanos { blackhole(graph.shippingMapper) })
    mapperItems.add("FeedMapper" to measureNanos { blackhole(graph.feedMapper) })
    layers.add(LayerBenchmarkResult("Mappers", 14, "Data transformation layer", mapperItems))

    // Layer 9: UseCases (28)
    val ucItems = mutableListOf<Pair<String, Long>>()
    ucItems.add("GetProductListUseCase" to measureNanos { blackhole(graph.getProductListUseCase) })
    ucItems.add("GetProductDetailUseCase" to measureNanos { blackhole(graph.getProductDetailUseCase) })
    ucItems.add("GetUserListUseCase" to measureNanos { blackhole(graph.getUserListUseCase) })
    ucItems.add("GetUserDetailUseCase" to measureNanos { blackhole(graph.getUserDetailUseCase) })
    ucItems.add("GetCartListUseCase" to measureNanos { blackhole(graph.getCartListUseCase) })
    ucItems.add("GetCartDetailUseCase" to measureNanos { blackhole(graph.getCartDetailUseCase) })
    ucItems.add("GetOrderListUseCase" to measureNanos { blackhole(graph.getOrderListUseCase) })
    ucItems.add("GetOrderDetailUseCase" to measureNanos { blackhole(graph.getOrderDetailUseCase) })
    ucItems.add("GetPaymentListUseCase" to measureNanos { blackhole(graph.getPaymentListUseCase) })
    ucItems.add("GetPaymentDetailUseCase" to measureNanos { blackhole(graph.getPaymentDetailUseCase) })
    ucItems.add("GetChatListUseCase" to measureNanos { blackhole(graph.getChatListUseCase) })
    ucItems.add("GetChatDetailUseCase" to measureNanos { blackhole(graph.getChatDetailUseCase) })
    ucItems.add("GetSearchListUseCase" to measureNanos { blackhole(graph.getSearchListUseCase) })
    ucItems.add("GetSearchDetailUseCase" to measureNanos { blackhole(graph.getSearchDetailUseCase) })
    ucItems.add("GetReviewListUseCase" to measureNanos { blackhole(graph.getReviewListUseCase) })
    ucItems.add("GetReviewDetailUseCase" to measureNanos { blackhole(graph.getReviewDetailUseCase) })
    ucItems.add("GetCategoryListUseCase" to measureNanos { blackhole(graph.getCategoryListUseCase) })
    ucItems.add("GetCategoryDetailUseCase" to measureNanos { blackhole(graph.getCategoryDetailUseCase) })
    ucItems.add("GetAddressListUseCase" to measureNanos { blackhole(graph.getAddressListUseCase) })
    ucItems.add("GetAddressDetailUseCase" to measureNanos { blackhole(graph.getAddressDetailUseCase) })
    ucItems.add("GetWishlistListUseCase" to measureNanos { blackhole(graph.getWishlistListUseCase) })
    ucItems.add("GetWishlistDetailUseCase" to measureNanos { blackhole(graph.getWishlistDetailUseCase) })
    ucItems.add("GetPromotionListUseCase" to measureNanos { blackhole(graph.getPromotionListUseCase) })
    ucItems.add("GetPromotionDetailUseCase" to measureNanos { blackhole(graph.getPromotionDetailUseCase) })
    ucItems.add("GetShippingListUseCase" to measureNanos { blackhole(graph.getShippingListUseCase) })
    ucItems.add("GetShippingDetailUseCase" to measureNanos { blackhole(graph.getShippingDetailUseCase) })
    ucItems.add("GetFeedListUseCase" to measureNanos { blackhole(graph.getFeedListUseCase) })
    ucItems.add("GetFeedDetailUseCase" to measureNanos { blackhole(graph.getFeedDetailUseCase) })
    layers.add(LayerBenchmarkResult("UseCases", 28, "Business logic / domain layer", ucItems))

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
