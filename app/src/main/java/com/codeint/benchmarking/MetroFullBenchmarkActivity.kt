package com.codeint.benchmarking

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeint.benchmarking.ui.theme.BenchMarkingTheme
import com.codeint.shopapp.metro.graph.ShopAppGraph
import com.codeint.shopapp.metro.graph.GraphFactory

/**
 * Full Metro module benchmark — mirrors HiltFullBenchmarkActivity.
 * Measures graph creation + every accessor: ViewModels, singletons, services, repositories, data sources, mappers, use cases.
 */
class MetroFullBenchmarkActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BenchMarkingTheme {
                var result by remember { mutableStateOf<FullBenchmarkResult?>(null) }
                var isRunning by remember { mutableStateOf(false) }

                Scaffold { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Metro Full Module Benchmark",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)
                        Text("123 classes | every layer | real timing",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isRunning = true
                                result = null
                                Thread {
                                    val r = runMetroBenchmark()
                                    result = r
                                    isRunning = false
                                    r.logDetailed("MetroFullBench")
                                }.start()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Running...", color = Color.White)
                            } else {
                                Text("Run Full Benchmark", color = Color.White)
                            }
                        }

                        if (result != null) {
                            val r = result!!
                            Spacer(Modifier.height(16.dp))

                            Card(modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Grand Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("${String.format("%.1f", r.grandTotalUs / 1000.0)}ms",
                                                style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("${r.totalClasses}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                            Text("classes", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            for (layer in r.layers) {
                                MetroLayerCard(layer)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    private fun runMetroBenchmark(): FullBenchmarkResult {
        val layers = mutableListOf<LayerResult>()

        // Graph creation — this is where Metro does ALL its work (compile-time generated factory)
        var graph: ShopAppGraph? = null
        val graphCreateTime = m { graph = GraphFactory.create() }
        val g = graph!!

        layers.add(LayerResult("Graph Creation", 1, "createGraphFactory<ShopAppGraph>()",
            listOf("ShopAppGraph" to graphCreateTime)))

        // ViewModels — accessing from graph (already constructed during graph creation for singletons)
        layers.add(LayerResult("ViewModels", 13, "@Inject constructor, graph accessor", listOf(
            "HomeViewModel" to m { g.homeViewModel },
            "SearchViewModel" to m { g.searchViewModel },
            "ProductDetailVM" to m { g.productDetailViewModel },
            "CartViewModel" to m { g.cartViewModel },
            "CheckoutViewModel" to m { g.checkoutViewModel },
            "ProfileViewModel" to m { g.profileViewModel },
            "ChatViewModel" to m { g.chatViewModel },
            "OrderHistoryVM" to m { g.orderHistoryViewModel },
            "SettingsViewModel" to m { g.settingsViewModel },
            "NotificationsVM" to m { g.notificationsViewModel },
            "OnboardingVM" to m { g.onboardingViewModel },
            "ReviewsViewModel" to m { g.reviewsViewModel },
            "WishlistViewModel" to m { g.wishlistViewModel },
        )))

        // Core Singletons — accessed via graph accessor properties
        layers.add(LayerResult("Core Singletons", 14, "@SingleIn(AppScope) via @Provides", listOf(
            "HttpClient" to m { g.httpClient },
            "AuthManager" to m { g.authManager },
            "TokenStorage" to m { g.tokenStorage },
            "SessionManager" to m { g.sessionManager },
            "AnalyticsTracker" to m { g.analyticsTracker },
            "CrashReporter" to m { g.crashReporter },
            "DatabaseManager" to m { g.databaseManager },
            "PreferencesManager" to m { g.preferencesManager },
            "SecureStorage" to m { g.secureStorage },
            "CacheManager" to m { g.cacheManager },
            "AppLogger" to m { g.appLogger },
            "TokenProvider" to m { g.tokenProvider },
            "NetworkLogger" to m { g.networkLogger },
            "CachePolicy" to m { g.cachePolicy },
        )))

        // Core Services
        layers.add(LayerResult("Core Services", 12, "@Inject, graph accessor", listOf(
            "AuthInterceptor" to m { g.authInterceptor },
            "RateLimiter" to m { g.rateLimiter },
            "WebSocketManager" to m { g.webSocketManager },
            "GraphQLClient" to m { g.graphQLClient },
            "ImageLoader" to m { g.imageLoader },
            "FeatureFlagManager" to m { g.featureFlagManager },
            "AppConfigProvider" to m { g.appConfigProvider },
            "NotificationManager" to m { g.notificationManager },
            "DeepLinkHandler" to m { g.deepLinkHandler },
            "LocationManager" to m { g.locationManager },
            "StoreLocator" to m { g.storeLocator },
            "AuditLogger" to m { g.auditLogger },
        )))

        // Repositories
        layers.add(LayerResult("Repositories", 14, "@SingleIn(AppScope), OfflineFirst", listOf(
            "ProductRepo" to m { g.productRepository },
            "UserRepo" to m { g.userRepository },
            "CartRepo" to m { g.cartRepository },
            "OrderRepo" to m { g.orderRepository },
            "PaymentRepo" to m { g.paymentRepository },
            "ChatRepo" to m { g.chatRepository },
            "SearchRepo" to m { g.searchRepository },
            "ReviewRepo" to m { g.reviewRepository },
            "CategoryRepo" to m { g.categoryRepository },
            "AddressRepo" to m { g.addressRepository },
            "WishlistRepo" to m { g.wishlistRepository },
            "PromotionRepo" to m { g.promotionRepository },
            "ShippingRepo" to m { g.shippingRepository },
            "FeedRepo" to m { g.feedRepository },
        )))

        // Remote Data Sources
        layers.add(LayerResult("RemoteDataSources", 14, "@Inject, no scope", listOf(
            "ProductRemote" to m { g.productRemoteDataSource },
            "UserRemote" to m { g.userRemoteDataSource },
            "CartRemote" to m { g.cartRemoteDataSource },
            "OrderRemote" to m { g.orderRemoteDataSource },
            "PaymentRemote" to m { g.paymentRemoteDataSource },
            "ChatRemote" to m { g.chatRemoteDataSource },
            "SearchRemote" to m { g.searchRemoteDataSource },
            "ReviewRemote" to m { g.reviewRemoteDataSource },
            "CategoryRemote" to m { g.categoryRemoteDataSource },
            "AddressRemote" to m { g.addressRemoteDataSource },
            "WishlistRemote" to m { g.wishlistRemoteDataSource },
            "PromotionRemote" to m { g.promotionRemoteDataSource },
            "ShippingRemote" to m { g.shippingRemoteDataSource },
            "FeedRemote" to m { g.feedRemoteDataSource },
        )))

        // Local Data Sources
        layers.add(LayerResult("LocalDataSources", 14, "@Inject, no scope", listOf(
            "ProductLocal" to m { g.productLocalDataSource },
            "UserLocal" to m { g.userLocalDataSource },
            "CartLocal" to m { g.cartLocalDataSource },
            "OrderLocal" to m { g.orderLocalDataSource },
            "PaymentLocal" to m { g.paymentLocalDataSource },
            "ChatLocal" to m { g.chatLocalDataSource },
            "SearchLocal" to m { g.searchLocalDataSource },
            "ReviewLocal" to m { g.reviewLocalDataSource },
            "CategoryLocal" to m { g.categoryLocalDataSource },
            "AddressLocal" to m { g.addressLocalDataSource },
            "WishlistLocal" to m { g.wishlistLocalDataSource },
            "PromotionLocal" to m { g.promotionLocalDataSource },
            "ShippingLocal" to m { g.shippingLocalDataSource },
            "FeedLocal" to m { g.feedLocalDataSource },
        )))

        // Mappers
        layers.add(LayerResult("Mappers", 14, "@Inject, no scope", listOf(
            "ProductMapper" to m { g.productMapper },
            "UserMapper" to m { g.userMapper },
            "CartMapper" to m { g.cartMapper },
            "OrderMapper" to m { g.orderMapper },
            "PaymentMapper" to m { g.paymentMapper },
            "ChatMapper" to m { g.chatMapper },
            "SearchMapper" to m { g.searchMapper },
            "ReviewMapper" to m { g.reviewMapper },
            "CategoryMapper" to m { g.categoryMapper },
            "AddressMapper" to m { g.addressMapper },
            "WishlistMapper" to m { g.wishlistMapper },
            "PromotionMapper" to m { g.promotionMapper },
            "ShippingMapper" to m { g.shippingMapper },
            "FeedMapper" to m { g.feedMapper },
        )))

        // Use Cases
        layers.add(LayerResult("UseCases", 28, "@Inject, factory (new each call)", listOf(
            "GetProductList" to m { g.getProductListUseCase },
            "GetProductDetail" to m { g.getProductDetailUseCase },
            "GetUserList" to m { g.getUserListUseCase },
            "GetUserDetail" to m { g.getUserDetailUseCase },
            "GetCartList" to m { g.getCartListUseCase },
            "GetCartDetail" to m { g.getCartDetailUseCase },
            "GetOrderList" to m { g.getOrderListUseCase },
            "GetOrderDetail" to m { g.getOrderDetailUseCase },
            "GetPaymentList" to m { g.getPaymentListUseCase },
            "GetPaymentDetail" to m { g.getPaymentDetailUseCase },
            "GetChatList" to m { g.getChatListUseCase },
            "GetChatDetail" to m { g.getChatDetailUseCase },
            "GetSearchList" to m { g.getSearchListUseCase },
            "GetSearchDetail" to m { g.getSearchDetailUseCase },
            "GetReviewList" to m { g.getReviewListUseCase },
            "GetReviewDetail" to m { g.getReviewDetailUseCase },
            "GetCategoryList" to m { g.getCategoryListUseCase },
            "GetCategoryDetail" to m { g.getCategoryDetailUseCase },
            "GetAddressList" to m { g.getAddressListUseCase },
            "GetAddressDetail" to m { g.getAddressDetailUseCase },
            "GetWishlistList" to m { g.getWishlistListUseCase },
            "GetWishlistDetail" to m { g.getWishlistDetailUseCase },
            "GetPromotionList" to m { g.getPromotionListUseCase },
            "GetPromotionDetail" to m { g.getPromotionDetailUseCase },
            "GetShippingList" to m { g.getShippingListUseCase },
            "GetShippingDetail" to m { g.getShippingDetailUseCase },
            "GetFeedList" to m { g.getFeedListUseCase },
            "GetFeedDetail" to m { g.getFeedDetailUseCase },
        )))

        return FullBenchmarkResult(layers)
    }

    private inline fun m(block: () -> Any?): Long {
        val s = System.nanoTime()
        block()
        return System.nanoTime() - s
    }
}

@Composable
private fun MetroLayerCard(layer: LayerResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${layer.name} (${layer.count})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(layer.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${String.format("%.2f", layer.totalNanos / 1_000_000.0)}ms",
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF7C4DFF))
            }
            Spacer(Modifier.height(8.dp))
            val sorted = layer.items.sortedByDescending { it.second }
            for ((name, nanos) in sorted.take(5)) {
                val ms = nanos / 1_000_000.0
                Row(Modifier.fillMaxWidth().padding(vertical = 1.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, fontSize = 11.sp)
                    Text("${String.format("%.2f", ms)}ms", fontSize = 11.sp,
                        fontWeight = if (ms > 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (ms > 5) Color.Red else if (ms > 1) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurface)
                }
            }
            if (sorted.size > 5) {
                Text("  ... +${sorted.size - 5} more", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
