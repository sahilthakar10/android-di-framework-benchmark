package com.codeint.benchmarking

import android.app.Application
import androidx.lifecycle.ViewModel
import com.codeint.shopapp.hilt.di.BenchmarkEntryPoint
import dagger.hilt.android.EntryPointAccessors

data class LayerResult(
    val name: String,
    val count: Int,
    val description: String,
    val items: List<Pair<String, Long>>,
) {
    val totalNanos get() = items.sumOf { it.second }
    val totalUs get() = totalNanos / 1000
    val avgUs get() = if (items.isNotEmpty()) totalUs / items.size else 0
}

data class FullBenchmarkResult(
    val layers: List<LayerResult>,
) {
    val grandTotalNanos get() = layers.sumOf { it.totalNanos }
    val grandTotalUs get() = grandTotalNanos / 1000
    val totalClasses get() = layers.sumOf { it.count }

    fun logDetailed(tag: String) {
        android.util.Log.i(tag, "════════════════════════════════════════")
        android.util.Log.i(tag, "Grand Total: ${grandTotalUs / 1000}ms (${totalClasses} classes)")
        android.util.Log.i(tag, "════════════════════════════════════════")
        for (layer in layers) {
            android.util.Log.i(tag, "── ${layer.name} (${layer.count}) ── ${"%.3f".format(layer.totalNanos / 1_000_000.0)}ms")
            for ((name, nanos) in layer.items.sortedByDescending { it.second }) {
                android.util.Log.i(tag, "  $name: ${"%.3f".format(nanos / 1_000_000.0)}ms")
            }
        }
        android.util.Log.i(tag, "════════════════════════════════════════")
    }
}

object HiltFullBenchmarkRunner {

    fun run(
        app: Application,
        viewModels: List<Pair<String, () -> Any?>>
    ): FullBenchmarkResult {
        val ep = EntryPointAccessors.fromApplication(app, BenchmarkEntryPoint::class.java)
        val layers = mutableListOf<LayerResult>()

        // ViewModels
        layers.add(LayerResult(
            "ViewModels", viewModels.size, "@HiltViewModel + ViewModelProvider",
            viewModels.map { (name, resolver) -> name to m { resolver() } }
        ))

        // Core Singletons
        layers.add(LayerResult(
            "Core Singletons", 14, "@Singleton via @Provides",
            listOf(
                "HttpClient" to m { ep.httpClient() },
                "AuthManager" to m { ep.authManager() },
                "TokenStorage" to m { ep.tokenStorage() },
                "SessionManager" to m { ep.sessionManager() },
                "AnalyticsTracker" to m { ep.analyticsTracker() },
                "CrashReporter" to m { ep.crashReporter() },
                "DatabaseManager" to m { ep.databaseManager() },
                "PreferencesManager" to m { ep.preferencesManager() },
                "SecureStorage" to m { ep.secureStorage() },
                "CacheManager" to m { ep.cacheManager() },
                "AppLogger" to m { ep.appLogger() },
                "TokenProvider" to m { ep.tokenProvider() },
                "NetworkLogger" to m { ep.networkLogger() },
                "CachePolicy" to m { ep.cachePolicy() },
            )
        ))

        // Core Services
        layers.add(LayerResult(
            "Core Services", 12, "@Inject constructor",
            listOf(
                "AuthInterceptor" to m { ep.authInterceptor() },
                "RateLimiter" to m { ep.rateLimiter() },
                "WebSocketManager" to m { ep.webSocketManager() },
                "GraphQLClient" to m { ep.graphQLClient() },
                "ImageLoader" to m { ep.imageLoader() },
                "FeatureFlagManager" to m { ep.featureFlagManager() },
                "AppConfigProvider" to m { ep.appConfigProvider() },
                "NotificationManager" to m { ep.notificationManager() },
                "DeepLinkHandler" to m { ep.deepLinkHandler() },
                "LocationManager" to m { ep.locationManager() },
                "StoreLocator" to m { ep.storeLocator() },
                "AuditLogger" to m { ep.auditLogger() },
            )
        ))

        // Repositories
        layers.add(LayerResult(
            "Repositories", 14, "OfflineFirst via @Provides",
            listOf(
                "ProductRepo" to m { ep.productRepository() },
                "UserRepo" to m { ep.userRepository() },
                "CartRepo" to m { ep.cartRepository() },
                "OrderRepo" to m { ep.orderRepository() },
                "PaymentRepo" to m { ep.paymentRepository() },
                "ChatRepo" to m { ep.chatRepository() },
                "SearchRepo" to m { ep.searchRepository() },
                "ReviewRepo" to m { ep.reviewRepository() },
                "CategoryRepo" to m { ep.categoryRepository() },
                "AddressRepo" to m { ep.addressRepository() },
                "WishlistRepo" to m { ep.wishlistRepository() },
                "PromotionRepo" to m { ep.promotionRepository() },
                "ShippingRepo" to m { ep.shippingRepository() },
                "FeedRepo" to m { ep.feedRepository() },
            )
        ))

        // Remote Data Sources
        layers.add(LayerResult(
            "RemoteDataSources", 14, "@Inject, no scope",
            listOf(
                "ProductRemote" to m { ep.productRemoteDataSource() },
                "UserRemote" to m { ep.userRemoteDataSource() },
                "CartRemote" to m { ep.cartRemoteDataSource() },
                "OrderRemote" to m { ep.orderRemoteDataSource() },
                "PaymentRemote" to m { ep.paymentRemoteDataSource() },
                "ChatRemote" to m { ep.chatRemoteDataSource() },
                "SearchRemote" to m { ep.searchRemoteDataSource() },
                "ReviewRemote" to m { ep.reviewRemoteDataSource() },
                "CategoryRemote" to m { ep.categoryRemoteDataSource() },
                "AddressRemote" to m { ep.addressRemoteDataSource() },
                "WishlistRemote" to m { ep.wishlistRemoteDataSource() },
                "PromotionRemote" to m { ep.promotionRemoteDataSource() },
                "ShippingRemote" to m { ep.shippingRemoteDataSource() },
                "FeedRemote" to m { ep.feedRemoteDataSource() },
            )
        ))

        // Local Data Sources
        layers.add(LayerResult(
            "LocalDataSources", 14, "@Inject, no scope",
            listOf(
                "ProductLocal" to m { ep.productLocalDataSource() },
                "UserLocal" to m { ep.userLocalDataSource() },
                "CartLocal" to m { ep.cartLocalDataSource() },
                "OrderLocal" to m { ep.orderLocalDataSource() },
                "PaymentLocal" to m { ep.paymentLocalDataSource() },
                "ChatLocal" to m { ep.chatLocalDataSource() },
                "SearchLocal" to m { ep.searchLocalDataSource() },
                "ReviewLocal" to m { ep.reviewLocalDataSource() },
                "CategoryLocal" to m { ep.categoryLocalDataSource() },
                "AddressLocal" to m { ep.addressLocalDataSource() },
                "WishlistLocal" to m { ep.wishlistLocalDataSource() },
                "PromotionLocal" to m { ep.promotionLocalDataSource() },
                "ShippingLocal" to m { ep.shippingLocalDataSource() },
                "FeedLocal" to m { ep.feedLocalDataSource() },
            )
        ))

        // Mappers
        layers.add(LayerResult(
            "Mappers", 14, "@Inject, no scope",
            listOf(
                "ProductMapper" to m { ep.productMapper() },
                "UserMapper" to m { ep.userMapper() },
                "CartMapper" to m { ep.cartMapper() },
                "OrderMapper" to m { ep.orderMapper() },
                "PaymentMapper" to m { ep.paymentMapper() },
                "ChatMapper" to m { ep.chatMapper() },
                "SearchMapper" to m { ep.searchMapper() },
                "ReviewMapper" to m { ep.reviewMapper() },
                "CategoryMapper" to m { ep.categoryMapper() },
                "AddressMapper" to m { ep.addressMapper() },
                "WishlistMapper" to m { ep.wishlistMapper() },
                "PromotionMapper" to m { ep.promotionMapper() },
                "ShippingMapper" to m { ep.shippingMapper() },
                "FeedMapper" to m { ep.feedMapper() },
            )
        ))

        // Use Cases
        layers.add(LayerResult(
            "UseCases", 28, "@Inject, factory (new each call)",
            listOf(
                "GetProductList" to m { ep.getProductListUseCase() },
                "GetUserList" to m { ep.getUserListUseCase() },
                "GetCartList" to m { ep.getCartListUseCase() },
                "GetOrderList" to m { ep.getOrderListUseCase() },
                "GetPaymentList" to m { ep.getPaymentListUseCase() },
                "GetChatList" to m { ep.getChatListUseCase() },
                "GetSearchList" to m { ep.getSearchListUseCase() },
                "GetReviewList" to m { ep.getReviewListUseCase() },
                "GetCategoryList" to m { ep.getCategoryListUseCase() },
                "GetAddressList" to m { ep.getAddressListUseCase() },
                "GetWishlistList" to m { ep.getWishlistListUseCase() },
                "GetPromotionList" to m { ep.getPromotionListUseCase() },
                "GetShippingList" to m { ep.getShippingListUseCase() },
                "GetFeedList" to m { ep.getFeedListUseCase() },
                "GetProductDetail" to m { ep.getProductDetailUseCase() },
                "GetUserDetail" to m { ep.getUserDetailUseCase() },
                "GetCartDetail" to m { ep.getCartDetailUseCase() },
                "GetOrderDetail" to m { ep.getOrderDetailUseCase() },
                "GetPaymentDetail" to m { ep.getPaymentDetailUseCase() },
                "GetChatDetail" to m { ep.getChatDetailUseCase() },
                "GetSearchDetail" to m { ep.getSearchDetailUseCase() },
                "GetReviewDetail" to m { ep.getReviewDetailUseCase() },
                "GetCategoryDetail" to m { ep.getCategoryDetailUseCase() },
                "GetAddressDetail" to m { ep.getAddressDetailUseCase() },
                "GetWishlistDetail" to m { ep.getWishlistDetailUseCase() },
                "GetPromotionDetail" to m { ep.getPromotionDetailUseCase() },
                "GetShippingDetail" to m { ep.getShippingDetailUseCase() },
                "GetFeedDetail" to m { ep.getFeedDetailUseCase() },
            )
        ))

        return FullBenchmarkResult(layers)
    }

    private inline fun m(block: () -> Any?): Long {
        val s = System.nanoTime()
        block()
        return System.nanoTime() - s
    }
}
