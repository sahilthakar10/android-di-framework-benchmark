package com.codeint.benchmarking

import android.os.Bundle
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
import com.codeint.shopapp.kinject.component.ComponentFactory
import com.codeint.shopapp.kinject.component.ShopAppComponent

/**
 * Full kotlin-inject-anvil module benchmark — mirrors MetroFullBenchmarkActivity.
 * Measures component creation + every accessor: ViewModels, singletons, services, repositories, data sources, mappers, use cases.
 */
class KinjectFullBenchmarkActivity : ComponentActivity() {

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
                        Text("kotlin-inject-anvil Full Module Benchmark",
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
                                    val r = runKinjectBenchmark()
                                    result = r
                                    isRunning = false
                                    r.logDetailed("KinjectFullBench")
                                }.start()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
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
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
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
                                KinjectLayerCard(layer)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    private fun runKinjectBenchmark(): FullBenchmarkResult {
        val layers = mutableListOf<LayerResult>()

        // Component creation
        var component: ShopAppComponent? = null
        val componentCreateTime = m { component = ComponentFactory.create() }
        val c = component!!

        layers.add(LayerResult("Component Creation", 1, "ComponentFactory.create()",
            listOf("ShopAppComponent" to componentCreateTime)))

        // ViewModels
        layers.add(LayerResult("ViewModels", 13, "@Inject constructor, component accessor", listOf(
            "HomeViewModel" to m { c.homeViewModel },
            "SearchViewModel" to m { c.searchViewModel },
            "ProductDetailVM" to m { c.productDetailViewModel },
            "CartViewModel" to m { c.cartViewModel },
            "CheckoutViewModel" to m { c.checkoutViewModel },
            "ProfileViewModel" to m { c.profileViewModel },
            "ChatViewModel" to m { c.chatViewModel },
            "OrderHistoryVM" to m { c.orderHistoryViewModel },
            "SettingsViewModel" to m { c.settingsViewModel },
            "NotificationsVM" to m { c.notificationsViewModel },
            "OnboardingVM" to m { c.onboardingViewModel },
            "ReviewsViewModel" to m { c.reviewsViewModel },
            "WishlistViewModel" to m { c.wishlistViewModel },
        )))

        // Core Singletons
        layers.add(LayerResult("Core Singletons", 14, "@SingleIn(AppScope) via @Provides", listOf(
            "HttpClient" to m { c.httpClient },
            "AuthManager" to m { c.authManager },
            "TokenStorage" to m { c.tokenStorage },
            "SessionManager" to m { c.sessionManager },
            "AnalyticsTracker" to m { c.analyticsTracker },
            "CrashReporter" to m { c.crashReporter },
            "DatabaseManager" to m { c.databaseManager },
            "PreferencesManager" to m { c.preferencesManager },
            "SecureStorage" to m { c.secureStorage },
            "CacheManager" to m { c.cacheManager },
            "AppLogger" to m { c.appLogger },
            "TokenProvider" to m { c.tokenProvider },
            "NetworkLogger" to m { c.networkLogger },
            "CachePolicy" to m { c.cachePolicy },
        )))

        // Core Services
        layers.add(LayerResult("Core Services", 12, "@Inject, component accessor", listOf(
            "AuthInterceptor" to m { c.authInterceptor },
            "RateLimiter" to m { c.rateLimiter },
            "WebSocketManager" to m { c.webSocketManager },
            "GraphQLClient" to m { c.graphQLClient },
            "ImageLoader" to m { c.imageLoader },
            "FeatureFlagManager" to m { c.featureFlagManager },
            "AppConfigProvider" to m { c.appConfigProvider },
            "NotificationManager" to m { c.notificationManager },
            "DeepLinkHandler" to m { c.deepLinkHandler },
            "LocationManager" to m { c.locationManager },
            "StoreLocator" to m { c.storeLocator },
            "AuditLogger" to m { c.auditLogger },
        )))

        // Repositories
        layers.add(LayerResult("Repositories", 14, "@SingleIn(AppScope), OfflineFirst", listOf(
            "ProductRepo" to m { c.productRepository },
            "UserRepo" to m { c.userRepository },
            "CartRepo" to m { c.cartRepository },
            "OrderRepo" to m { c.orderRepository },
            "PaymentRepo" to m { c.paymentRepository },
            "ChatRepo" to m { c.chatRepository },
            "SearchRepo" to m { c.searchRepository },
            "ReviewRepo" to m { c.reviewRepository },
            "CategoryRepo" to m { c.categoryRepository },
            "AddressRepo" to m { c.addressRepository },
            "WishlistRepo" to m { c.wishlistRepository },
            "PromotionRepo" to m { c.promotionRepository },
            "ShippingRepo" to m { c.shippingRepository },
            "FeedRepo" to m { c.feedRepository },
        )))

        // Remote Data Sources
        layers.add(LayerResult("RemoteDataSources", 14, "@Inject, no scope", listOf(
            "ProductRemote" to m { c.productRemoteDataSource },
            "UserRemote" to m { c.userRemoteDataSource },
            "CartRemote" to m { c.cartRemoteDataSource },
            "OrderRemote" to m { c.orderRemoteDataSource },
            "PaymentRemote" to m { c.paymentRemoteDataSource },
            "ChatRemote" to m { c.chatRemoteDataSource },
            "SearchRemote" to m { c.searchRemoteDataSource },
            "ReviewRemote" to m { c.reviewRemoteDataSource },
            "CategoryRemote" to m { c.categoryRemoteDataSource },
            "AddressRemote" to m { c.addressRemoteDataSource },
            "WishlistRemote" to m { c.wishlistRemoteDataSource },
            "PromotionRemote" to m { c.promotionRemoteDataSource },
            "ShippingRemote" to m { c.shippingRemoteDataSource },
            "FeedRemote" to m { c.feedRemoteDataSource },
        )))

        // Local Data Sources
        layers.add(LayerResult("LocalDataSources", 14, "@Inject, no scope", listOf(
            "ProductLocal" to m { c.productLocalDataSource },
            "UserLocal" to m { c.userLocalDataSource },
            "CartLocal" to m { c.cartLocalDataSource },
            "OrderLocal" to m { c.orderLocalDataSource },
            "PaymentLocal" to m { c.paymentLocalDataSource },
            "ChatLocal" to m { c.chatLocalDataSource },
            "SearchLocal" to m { c.searchLocalDataSource },
            "ReviewLocal" to m { c.reviewLocalDataSource },
            "CategoryLocal" to m { c.categoryLocalDataSource },
            "AddressLocal" to m { c.addressLocalDataSource },
            "WishlistLocal" to m { c.wishlistLocalDataSource },
            "PromotionLocal" to m { c.promotionLocalDataSource },
            "ShippingLocal" to m { c.shippingLocalDataSource },
            "FeedLocal" to m { c.feedLocalDataSource },
        )))

        // Mappers
        layers.add(LayerResult("Mappers", 14, "@Inject, no scope", listOf(
            "ProductMapper" to m { c.productMapper },
            "UserMapper" to m { c.userMapper },
            "CartMapper" to m { c.cartMapper },
            "OrderMapper" to m { c.orderMapper },
            "PaymentMapper" to m { c.paymentMapper },
            "ChatMapper" to m { c.chatMapper },
            "SearchMapper" to m { c.searchMapper },
            "ReviewMapper" to m { c.reviewMapper },
            "CategoryMapper" to m { c.categoryMapper },
            "AddressMapper" to m { c.addressMapper },
            "WishlistMapper" to m { c.wishlistMapper },
            "PromotionMapper" to m { c.promotionMapper },
            "ShippingMapper" to m { c.shippingMapper },
            "FeedMapper" to m { c.feedMapper },
        )))

        // Use Cases
        layers.add(LayerResult("UseCases", 28, "@Inject, factory (new each call)", listOf(
            "GetProductList" to m { c.getProductListUseCase },
            "GetProductDetail" to m { c.getProductDetailUseCase },
            "GetUserList" to m { c.getUserListUseCase },
            "GetUserDetail" to m { c.getUserDetailUseCase },
            "GetCartList" to m { c.getCartListUseCase },
            "GetCartDetail" to m { c.getCartDetailUseCase },
            "GetOrderList" to m { c.getOrderListUseCase },
            "GetOrderDetail" to m { c.getOrderDetailUseCase },
            "GetPaymentList" to m { c.getPaymentListUseCase },
            "GetPaymentDetail" to m { c.getPaymentDetailUseCase },
            "GetChatList" to m { c.getChatListUseCase },
            "GetChatDetail" to m { c.getChatDetailUseCase },
            "GetSearchList" to m { c.getSearchListUseCase },
            "GetSearchDetail" to m { c.getSearchDetailUseCase },
            "GetReviewList" to m { c.getReviewListUseCase },
            "GetReviewDetail" to m { c.getReviewDetailUseCase },
            "GetCategoryList" to m { c.getCategoryListUseCase },
            "GetCategoryDetail" to m { c.getCategoryDetailUseCase },
            "GetAddressList" to m { c.getAddressListUseCase },
            "GetAddressDetail" to m { c.getAddressDetailUseCase },
            "GetWishlistList" to m { c.getWishlistListUseCase },
            "GetWishlistDetail" to m { c.getWishlistDetailUseCase },
            "GetPromotionList" to m { c.getPromotionListUseCase },
            "GetPromotionDetail" to m { c.getPromotionDetailUseCase },
            "GetShippingList" to m { c.getShippingListUseCase },
            "GetShippingDetail" to m { c.getShippingDetailUseCase },
            "GetFeedList" to m { c.getFeedListUseCase },
            "GetFeedDetail" to m { c.getFeedDetailUseCase },
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
private fun KinjectLayerCard(layer: LayerResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${layer.name} (${layer.count})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(layer.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${String.format("%.2f", layer.totalNanos / 1_000_000.0)}ms",
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
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
