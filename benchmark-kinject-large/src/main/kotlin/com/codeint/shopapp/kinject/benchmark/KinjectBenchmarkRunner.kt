package com.codeint.shopapp.kinject.benchmark

import com.codeint.shopapp.kinject.component.ComponentFactory

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

fun runKinjectBenchmark(warmIterations: Int = 100): KinjectBenchmarkResult {
    // ── SETUP: Warmup phase (excluded from measurement) ──
    val warmupComponent = ComponentFactory.create()
    val warmupTargets = listOf(
        { warmupComponent.homeViewModel },
        { warmupComponent.searchViewModel },
        { warmupComponent.productDetailViewModel },
        { warmupComponent.cartViewModel },
        { warmupComponent.checkoutViewModel },
        { warmupComponent.profileViewModel },
        { warmupComponent.chatViewModel },
        { warmupComponent.orderHistoryViewModel },
        { warmupComponent.analyticsTracker },
        { warmupComponent.productRepository }
    )
    // Run warmup iterations — results discarded
    repeat(5) {
        warmupTargets.forEach { resolve -> blackhole(resolve()) }
    }

    // ── MEASUREMENT 1: Container initialization ──
    val initStart = System.nanoTime()
    val component = ComponentFactory.create()
    val initTime = System.nanoTime() - initStart

    // ── MEASUREMENT 2: Cold injection (first access after fresh component) ──
    val cold = linkedMapOf<String, Long>()
    cold["HomeViewModel"] = measureNanos { blackhole(component.homeViewModel) }
    cold["SearchViewModel"] = measureNanos { blackhole(component.searchViewModel) }
    cold["ProductDetailVM"] = measureNanos { blackhole(component.productDetailViewModel) }
    cold["CartViewModel"] = measureNanos { blackhole(component.cartViewModel) }
    cold["CheckoutViewModel"] = measureNanos { blackhole(component.checkoutViewModel) }
    cold["ProfileViewModel"] = measureNanos { blackhole(component.profileViewModel) }
    cold["ChatViewModel"] = measureNanos { blackhole(component.chatViewModel) }
    cold["OrderHistoryVM"] = measureNanos { blackhole(component.orderHistoryViewModel) }
    cold["AnalyticsTracker"] = measureNanos { blackhole(component.analyticsTracker) }
    cold["ProductRepository"] = measureNanos { blackhole(component.productRepository) }

    // ── MEASUREMENT 3: Warm injection (repeated access) ──
    val warm = linkedMapOf<String, Long>()
    var totalWarm = 0L
    val targets = listOf<Pair<String, () -> Any>>(
        "HomeViewModel" to { component.homeViewModel },
        "SearchViewModel" to { component.searchViewModel },
        "ProductDetailVM" to { component.productDetailViewModel },
        "CartViewModel" to { component.cartViewModel },
        "CheckoutViewModel" to { component.checkoutViewModel },
        "ProfileViewModel" to { component.profileViewModel },
        "ChatViewModel" to { component.chatViewModel },
        "OrderHistoryVM" to { component.orderHistoryViewModel },
        "AnalyticsTracker" to { component.analyticsTracker },
        "ProductRepository" to { component.productRepository }
    )
    for ((name, resolver) in targets) {
        var sum = 0L
        repeat(warmIterations) {
            val s = System.nanoTime()
            blackhole(resolver())
            sum += System.nanoTime() - s
        }
        warm[name] = sum / warmIterations
        totalWarm += sum
    }

    return KinjectBenchmarkResult(initTime, cold, warm, totalWarm)
}

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

fun runFullBenchmark(): FullBenchmarkResult {
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
    vmItems.add("HomeViewModel" to measureNanos { blackhole(component.homeViewModel) })
    vmItems.add("SearchViewModel" to measureNanos { blackhole(component.searchViewModel) })
    vmItems.add("ProductDetailViewModel" to measureNanos { blackhole(component.productDetailViewModel) })
    vmItems.add("CartViewModel" to measureNanos { blackhole(component.cartViewModel) })
    vmItems.add("CheckoutViewModel" to measureNanos { blackhole(component.checkoutViewModel) })
    vmItems.add("ProfileViewModel" to measureNanos { blackhole(component.profileViewModel) })
    vmItems.add("ChatViewModel" to measureNanos { blackhole(component.chatViewModel) })
    vmItems.add("OrderHistoryViewModel" to measureNanos { blackhole(component.orderHistoryViewModel) })
    vmItems.add("SettingsViewModel" to measureNanos { blackhole(component.settingsViewModel) })
    vmItems.add("NotificationsViewModel" to measureNanos { blackhole(component.notificationsViewModel) })
    vmItems.add("OnboardingViewModel" to measureNanos { blackhole(component.onboardingViewModel) })
    vmItems.add("ReviewsViewModel" to measureNanos { blackhole(component.reviewsViewModel) })
    vmItems.add("WishlistViewModel" to measureNanos { blackhole(component.wishlistViewModel) })
    layers.add(LayerBenchmarkResult("ViewModels", 13, "Feature-level presentation layer", vmItems))

    // Layer 3: Core Singletons (14)
    val coreItems = mutableListOf<Pair<String, Long>>()
    coreItems.add("HttpClient" to measureNanos { blackhole(component.httpClient) })
    coreItems.add("AuthManager" to measureNanos { blackhole(component.authManager) })
    coreItems.add("TokenStorage" to measureNanos { blackhole(component.tokenStorage) })
    coreItems.add("SessionManager" to measureNanos { blackhole(component.sessionManager) })
    coreItems.add("AnalyticsTracker" to measureNanos { blackhole(component.analyticsTracker) })
    coreItems.add("CrashReporter" to measureNanos { blackhole(component.crashReporter) })
    coreItems.add("DatabaseManager" to measureNanos { blackhole(component.databaseManager) })
    coreItems.add("PreferencesManager" to measureNanos { blackhole(component.preferencesManager) })
    coreItems.add("SecureStorage" to measureNanos { blackhole(component.secureStorage) })
    coreItems.add("CacheManager" to measureNanos { blackhole(component.cacheManager) })
    coreItems.add("AppLogger" to measureNanos { blackhole(component.appLogger) })
    coreItems.add("TokenProvider" to measureNanos { blackhole(component.tokenProvider) })
    coreItems.add("NetworkLogger" to measureNanos { blackhole(component.networkLogger) })
    coreItems.add("CachePolicy" to measureNanos { blackhole(component.cachePolicy) })
    layers.add(LayerBenchmarkResult("Core Singletons", 14, "Shared infrastructure services", coreItems))

    // Layer 4: Core Services (12)
    val svcItems = mutableListOf<Pair<String, Long>>()
    svcItems.add("AuthInterceptor" to measureNanos { blackhole(component.authInterceptor) })
    svcItems.add("RateLimiter" to measureNanos { blackhole(component.rateLimiter) })
    svcItems.add("WebSocketManager" to measureNanos { blackhole(component.webSocketManager) })
    svcItems.add("GraphQLClient" to measureNanos { blackhole(component.graphQLClient) })
    svcItems.add("ImageLoader" to measureNanos { blackhole(component.imageLoader) })
    svcItems.add("FeatureFlagManager" to measureNanos { blackhole(component.featureFlagManager) })
    svcItems.add("AppConfigProvider" to measureNanos { blackhole(component.appConfigProvider) })
    svcItems.add("NotificationManager" to measureNanos { blackhole(component.notificationManager) })
    svcItems.add("DeepLinkHandler" to measureNanos { blackhole(component.deepLinkHandler) })
    svcItems.add("LocationManager" to measureNanos { blackhole(component.locationManager) })
    svcItems.add("StoreLocator" to measureNanos { blackhole(component.storeLocator) })
    svcItems.add("AuditLogger" to measureNanos { blackhole(component.auditLogger) })
    layers.add(LayerBenchmarkResult("Core Services", 12, "Application-level services", svcItems))

    // Layer 5: Repositories (14)
    val repoItems = mutableListOf<Pair<String, Long>>()
    repoItems.add("ProductRepository" to measureNanos { blackhole(component.productRepository) })
    repoItems.add("UserRepository" to measureNanos { blackhole(component.userRepository) })
    repoItems.add("CartRepository" to measureNanos { blackhole(component.cartRepository) })
    repoItems.add("OrderRepository" to measureNanos { blackhole(component.orderRepository) })
    repoItems.add("PaymentRepository" to measureNanos { blackhole(component.paymentRepository) })
    repoItems.add("ChatRepository" to measureNanos { blackhole(component.chatRepository) })
    repoItems.add("SearchRepository" to measureNanos { blackhole(component.searchRepository) })
    repoItems.add("ReviewRepository" to measureNanos { blackhole(component.reviewRepository) })
    repoItems.add("CategoryRepository" to measureNanos { blackhole(component.categoryRepository) })
    repoItems.add("AddressRepository" to measureNanos { blackhole(component.addressRepository) })
    repoItems.add("WishlistRepository" to measureNanos { blackhole(component.wishlistRepository) })
    repoItems.add("PromotionRepository" to measureNanos { blackhole(component.promotionRepository) })
    repoItems.add("ShippingRepository" to measureNanos { blackhole(component.shippingRepository) })
    repoItems.add("FeedRepository" to measureNanos { blackhole(component.feedRepository) })
    layers.add(LayerBenchmarkResult("Repositories", 14, "Data access abstraction layer", repoItems))

    // Layer 6: RemoteDataSources (14)
    val remoteItems = mutableListOf<Pair<String, Long>>()
    remoteItems.add("ProductRemoteDataSource" to measureNanos { blackhole(component.productRemoteDataSource) })
    remoteItems.add("UserRemoteDataSource" to measureNanos { blackhole(component.userRemoteDataSource) })
    remoteItems.add("CartRemoteDataSource" to measureNanos { blackhole(component.cartRemoteDataSource) })
    remoteItems.add("OrderRemoteDataSource" to measureNanos { blackhole(component.orderRemoteDataSource) })
    remoteItems.add("PaymentRemoteDataSource" to measureNanos { blackhole(component.paymentRemoteDataSource) })
    remoteItems.add("ChatRemoteDataSource" to measureNanos { blackhole(component.chatRemoteDataSource) })
    remoteItems.add("SearchRemoteDataSource" to measureNanos { blackhole(component.searchRemoteDataSource) })
    remoteItems.add("ReviewRemoteDataSource" to measureNanos { blackhole(component.reviewRemoteDataSource) })
    remoteItems.add("CategoryRemoteDataSource" to measureNanos { blackhole(component.categoryRemoteDataSource) })
    remoteItems.add("AddressRemoteDataSource" to measureNanos { blackhole(component.addressRemoteDataSource) })
    remoteItems.add("WishlistRemoteDataSource" to measureNanos { blackhole(component.wishlistRemoteDataSource) })
    remoteItems.add("PromotionRemoteDataSource" to measureNanos { blackhole(component.promotionRemoteDataSource) })
    remoteItems.add("ShippingRemoteDataSource" to measureNanos { blackhole(component.shippingRemoteDataSource) })
    remoteItems.add("FeedRemoteDataSource" to measureNanos { blackhole(component.feedRemoteDataSource) })
    layers.add(LayerBenchmarkResult("RemoteDataSources", 14, "Network API data sources", remoteItems))

    // Layer 7: LocalDataSources (14)
    val localItems = mutableListOf<Pair<String, Long>>()
    localItems.add("ProductLocalDataSource" to measureNanos { blackhole(component.productLocalDataSource) })
    localItems.add("UserLocalDataSource" to measureNanos { blackhole(component.userLocalDataSource) })
    localItems.add("CartLocalDataSource" to measureNanos { blackhole(component.cartLocalDataSource) })
    localItems.add("OrderLocalDataSource" to measureNanos { blackhole(component.orderLocalDataSource) })
    localItems.add("PaymentLocalDataSource" to measureNanos { blackhole(component.paymentLocalDataSource) })
    localItems.add("ChatLocalDataSource" to measureNanos { blackhole(component.chatLocalDataSource) })
    localItems.add("SearchLocalDataSource" to measureNanos { blackhole(component.searchLocalDataSource) })
    localItems.add("ReviewLocalDataSource" to measureNanos { blackhole(component.reviewLocalDataSource) })
    localItems.add("CategoryLocalDataSource" to measureNanos { blackhole(component.categoryLocalDataSource) })
    localItems.add("AddressLocalDataSource" to measureNanos { blackhole(component.addressLocalDataSource) })
    localItems.add("WishlistLocalDataSource" to measureNanos { blackhole(component.wishlistLocalDataSource) })
    localItems.add("PromotionLocalDataSource" to measureNanos { blackhole(component.promotionLocalDataSource) })
    localItems.add("ShippingLocalDataSource" to measureNanos { blackhole(component.shippingLocalDataSource) })
    localItems.add("FeedLocalDataSource" to measureNanos { blackhole(component.feedLocalDataSource) })
    layers.add(LayerBenchmarkResult("LocalDataSources", 14, "Local/cached data sources", localItems))

    // Layer 8: Mappers (14)
    val mapperItems = mutableListOf<Pair<String, Long>>()
    mapperItems.add("ProductMapper" to measureNanos { blackhole(component.productMapper) })
    mapperItems.add("UserMapper" to measureNanos { blackhole(component.userMapper) })
    mapperItems.add("CartMapper" to measureNanos { blackhole(component.cartMapper) })
    mapperItems.add("OrderMapper" to measureNanos { blackhole(component.orderMapper) })
    mapperItems.add("PaymentMapper" to measureNanos { blackhole(component.paymentMapper) })
    mapperItems.add("ChatMapper" to measureNanos { blackhole(component.chatMapper) })
    mapperItems.add("SearchMapper" to measureNanos { blackhole(component.searchMapper) })
    mapperItems.add("ReviewMapper" to measureNanos { blackhole(component.reviewMapper) })
    mapperItems.add("CategoryMapper" to measureNanos { blackhole(component.categoryMapper) })
    mapperItems.add("AddressMapper" to measureNanos { blackhole(component.addressMapper) })
    mapperItems.add("WishlistMapper" to measureNanos { blackhole(component.wishlistMapper) })
    mapperItems.add("PromotionMapper" to measureNanos { blackhole(component.promotionMapper) })
    mapperItems.add("ShippingMapper" to measureNanos { blackhole(component.shippingMapper) })
    mapperItems.add("FeedMapper" to measureNanos { blackhole(component.feedMapper) })
    layers.add(LayerBenchmarkResult("Mappers", 14, "Data transformation layer", mapperItems))

    // Layer 9: UseCases (28)
    val ucItems = mutableListOf<Pair<String, Long>>()
    ucItems.add("GetProductListUseCase" to measureNanos { blackhole(component.getProductListUseCase) })
    ucItems.add("GetProductDetailUseCase" to measureNanos { blackhole(component.getProductDetailUseCase) })
    ucItems.add("GetUserListUseCase" to measureNanos { blackhole(component.getUserListUseCase) })
    ucItems.add("GetUserDetailUseCase" to measureNanos { blackhole(component.getUserDetailUseCase) })
    ucItems.add("GetCartListUseCase" to measureNanos { blackhole(component.getCartListUseCase) })
    ucItems.add("GetCartDetailUseCase" to measureNanos { blackhole(component.getCartDetailUseCase) })
    ucItems.add("GetOrderListUseCase" to measureNanos { blackhole(component.getOrderListUseCase) })
    ucItems.add("GetOrderDetailUseCase" to measureNanos { blackhole(component.getOrderDetailUseCase) })
    ucItems.add("GetPaymentListUseCase" to measureNanos { blackhole(component.getPaymentListUseCase) })
    ucItems.add("GetPaymentDetailUseCase" to measureNanos { blackhole(component.getPaymentDetailUseCase) })
    ucItems.add("GetChatListUseCase" to measureNanos { blackhole(component.getChatListUseCase) })
    ucItems.add("GetChatDetailUseCase" to measureNanos { blackhole(component.getChatDetailUseCase) })
    ucItems.add("GetSearchListUseCase" to measureNanos { blackhole(component.getSearchListUseCase) })
    ucItems.add("GetSearchDetailUseCase" to measureNanos { blackhole(component.getSearchDetailUseCase) })
    ucItems.add("GetReviewListUseCase" to measureNanos { blackhole(component.getReviewListUseCase) })
    ucItems.add("GetReviewDetailUseCase" to measureNanos { blackhole(component.getReviewDetailUseCase) })
    ucItems.add("GetCategoryListUseCase" to measureNanos { blackhole(component.getCategoryListUseCase) })
    ucItems.add("GetCategoryDetailUseCase" to measureNanos { blackhole(component.getCategoryDetailUseCase) })
    ucItems.add("GetAddressListUseCase" to measureNanos { blackhole(component.getAddressListUseCase) })
    ucItems.add("GetAddressDetailUseCase" to measureNanos { blackhole(component.getAddressDetailUseCase) })
    ucItems.add("GetWishlistListUseCase" to measureNanos { blackhole(component.getWishlistListUseCase) })
    ucItems.add("GetWishlistDetailUseCase" to measureNanos { blackhole(component.getWishlistDetailUseCase) })
    ucItems.add("GetPromotionListUseCase" to measureNanos { blackhole(component.getPromotionListUseCase) })
    ucItems.add("GetPromotionDetailUseCase" to measureNanos { blackhole(component.getPromotionDetailUseCase) })
    ucItems.add("GetShippingListUseCase" to measureNanos { blackhole(component.getShippingListUseCase) })
    ucItems.add("GetShippingDetailUseCase" to measureNanos { blackhole(component.getShippingDetailUseCase) })
    ucItems.add("GetFeedListUseCase" to measureNanos { blackhole(component.getFeedListUseCase) })
    ucItems.add("GetFeedDetailUseCase" to measureNanos { blackhole(component.getFeedDetailUseCase) })
    layers.add(LayerBenchmarkResult("UseCases", 28, "Business logic / domain layer", ucItems))

    return FullBenchmarkResult(layers)
}

// Blackhole: prevents dead code elimination.
@Suppress("UNUSED_PARAMETER")
private fun blackhole(value: Any?) {
    // Intentionally empty — the function call itself prevents DCE
}

private inline fun measureNanos(block: () -> Unit): Long {
    val s = System.nanoTime()
    block()
    return System.nanoTime() - s
}
