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
import com.codeint.shopapp.koin.di.*
import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.core.auth.*
import com.codeint.shopapp.koin.core.analytics.*
import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.core.config.*
import com.codeint.shopapp.koin.core.logging.*
import com.codeint.shopapp.koin.core.image.*
import com.codeint.shopapp.koin.core.notification.*
import com.codeint.shopapp.koin.core.location.*
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.GlobalContext

/**
 * Full Koin module benchmark — mirrors HiltFullBenchmarkActivity.
 * Measures every layer: ViewModels, singletons, repositories, data sources, mappers, use cases.
 */
class KoinFullBenchmarkActivity : ComponentActivity() {

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
                        Text("Koin Full Module Benchmark",
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
                                    val r = runKoinBenchmark()
                                    result = r
                                    isRunning = false
                                    r.logDetailed("KoinFullBench")
                                }.start()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
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
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
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
                                KoinLayerCard(layer)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    private fun runKoinBenchmark(): FullBenchmarkResult {
        // Start fresh Koin
        try { stopKoin() } catch (_: Exception) {}
        val initStart = System.nanoTime()
        val koinApp = startKoin { modules(allShopAppModules) }
        val initTime = System.nanoTime() - initStart
        val koin = koinApp.koin

        val layers = mutableListOf<LayerResult>()

        // Container init as separate "layer"
        layers.add(LayerResult("Container Init", 1, "startKoin { modules(allShopAppModules) }",
            listOf("startKoin" to initTime)))

        // ViewModels — via koin.get<T>()
        layers.add(LayerResult("ViewModels", 13, "viewModel { } DSL via Koin", listOf(
            "HomeViewModel" to m { koin.get<HomeViewModel>() },
            "SearchViewModel" to m { koin.get<SearchViewModel>() },
            "ProductDetailVM" to m { koin.get<ProductDetailViewModel>() },
            "CartViewModel" to m { koin.get<CartViewModel>() },
            "CheckoutViewModel" to m { koin.get<CheckoutViewModel>() },
            "ProfileViewModel" to m { koin.get<ProfileViewModel>() },
            "ChatViewModel" to m { koin.get<ChatViewModel>() },
            "OrderHistoryVM" to m { koin.get<OrderHistoryViewModel>() },
            "SettingsViewModel" to m { koin.get<SettingsViewModel>() },
            "NotificationsVM" to m { koin.get<NotificationsViewModel>() },
            "OnboardingVM" to m { koin.get<OnboardingViewModel>() },
            "ReviewsViewModel" to m { koin.get<ReviewsViewModel>() },
            "WishlistViewModel" to m { koin.get<WishlistViewModel>() },
        )))

        // Core Singletons
        layers.add(LayerResult("Core Singletons", 14, "single { } with createdAtStart", listOf(
            "HttpClient" to m { koin.get<HttpClient>() },
            "AuthManager" to m { koin.get<AuthManager>() },
            "TokenStorage" to m { koin.get<TokenStorage>() },
            "SessionManager" to m { koin.get<SessionManager>() },
            "AnalyticsTracker" to m { koin.get<AnalyticsTracker>() },
            "CrashReporter" to m { koin.get<CrashReporter>() },
            "DatabaseManager" to m { koin.get<DatabaseManager>() },
            "PreferencesManager" to m { koin.get<PreferencesManager>() },
            "SecureStorage" to m { koin.get<SecureStorage>() },
            "CacheManager" to m { koin.get<CacheManager>() },
            "AppLogger" to m { koin.get<AppLogger>() },
            "TokenProvider" to m { koin.get<TokenProvider>() },
            "NetworkLogger" to m { koin.get<NetworkLogger>() },
            "CachePolicy" to m { koin.get<CachePolicy>() },
        )))

        // Core Services
        layers.add(LayerResult("Core Services", 12, "single { } runtime resolved", listOf(
            "AuthInterceptor" to m { koin.get<AuthInterceptor>() },
            "RateLimiter" to m { koin.get<RateLimiter>() },
            "WebSocketManager" to m { koin.get<WebSocketManager>() },
            "GraphQLClient" to m { koin.get<GraphQLClient>() },
            "ImageLoader" to m { koin.get<ImageLoader>() },
            "FeatureFlagManager" to m { koin.get<FeatureFlagManager>() },
            "AppConfigProvider" to m { koin.get<AppConfigProvider>() },
            "NotificationMgr" to m { koin.get<com.codeint.shopapp.koin.core.notification.NotificationManager>() },
            "DeepLinkHandler" to m { koin.get<DeepLinkHandler>() },
            "LocationManager" to m { koin.get<com.codeint.shopapp.koin.core.location.LocationManager>() },
            "StoreLocator" to m { koin.get<StoreLocator>() },
            "AuditLogger" to m { koin.get<AuditLogger>() },
        )))

        // Repositories
        val repoItems = mutableListOf<Pair<String, Long>>()
        val domains = listOf("product","user","cart","order","payment","chat","search","review","category","address","wishlist","promotion","shipping","feed")
        for (d in domains) {
            val cap = d.replaceFirstChar { it.uppercase() }
            // Use class reference directly
            repoItems.add("${cap}Repo" to m { koin.get<Any>(clazz = Class.forName("${PKG}.data.${d}.${cap}Repository").kotlin) })
        }
        layers.add(LayerResult("Repositories", 14, "OfflineFirst via single { }", repoItems))

        // Use Cases (sample - GetXxxListUseCase)
        val ucItems = mutableListOf<Pair<String, Long>>()
        for (d in domains) {
            val cap = d.replaceFirstChar { it.uppercase() }
            ucItems.add("Get${cap}List" to m { koin.get<Any>(clazz = Class.forName("${PKG}.domain.${d}.Get${cap}ListUseCase").kotlin) })
            ucItems.add("Get${cap}Detail" to m { koin.get<Any>(clazz = Class.forName("${PKG}.domain.${d}.Get${cap}DetailUseCase").kotlin) })
        }
        layers.add(LayerResult("UseCases", 28, "factory { } new each call", ucItems))

        stopKoin()
        return FullBenchmarkResult(layers)
    }

    private inline fun m(block: () -> Any?): Long {
        val s = System.nanoTime()
        block()
        return System.nanoTime() - s
    }

    companion object {
        private const val PKG = "com.codeint.shopapp.koin"
    }
}

@Composable
private fun KoinLayerCard(layer: LayerResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${layer.name} (${layer.count})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(layer.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${String.format("%.1f", layer.totalUs / 1000.0)}ms",
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
            }
            Spacer(Modifier.height(8.dp))
            val sorted = layer.items.sortedByDescending { it.second }
            for ((name, nanos) in sorted.take(5)) {
                val ms = nanos / 1000.0 / 1000.0
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
