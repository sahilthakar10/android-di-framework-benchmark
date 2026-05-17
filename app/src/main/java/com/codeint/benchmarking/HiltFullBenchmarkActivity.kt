package com.codeint.benchmarking

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.codeint.shopapp.hilt.feature.home.HomeViewModel
import com.codeint.shopapp.hilt.feature.search.SearchViewModel
import com.codeint.shopapp.hilt.feature.productdetail.ProductDetailViewModel
import com.codeint.shopapp.hilt.feature.cart.CartViewModel
import com.codeint.shopapp.hilt.feature.checkout.CheckoutViewModel
import com.codeint.shopapp.hilt.feature.profile.ProfileViewModel
import com.codeint.shopapp.hilt.feature.chat.ChatViewModel
import com.codeint.shopapp.hilt.feature.orders.OrderHistoryViewModel
import com.codeint.shopapp.hilt.feature.settings.SettingsViewModel
import com.codeint.shopapp.hilt.feature.notifications.NotificationsViewModel
import com.codeint.shopapp.hilt.feature.onboarding.OnboardingViewModel
import com.codeint.shopapp.hilt.feature.reviews.ReviewsViewModel
import com.codeint.shopapp.hilt.feature.wishlist.WishlistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltFullBenchmarkActivity : ComponentActivity() {

    private val homeVM: HomeViewModel by viewModels()
    private val searchVM: SearchViewModel by viewModels()
    private val productDetailVM: ProductDetailViewModel by viewModels()
    private val cartVM: CartViewModel by viewModels()
    private val checkoutVM: CheckoutViewModel by viewModels()
    private val profileVM: ProfileViewModel by viewModels()
    private val chatVM: ChatViewModel by viewModels()
    private val orderHistoryVM: OrderHistoryViewModel by viewModels()
    private val settingsVM: SettingsViewModel by viewModels()
    private val notificationsVM: NotificationsViewModel by viewModels()
    private val onboardingVM: OnboardingViewModel by viewModels()
    private val reviewsVM: ReviewsViewModel by viewModels()
    private val wishlistVM: WishlistViewModel by viewModels()

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
                        Text("Hilt Full Module Benchmark",
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
                                    val vmResolvers = listOf(
                                        "HomeViewModel" to { homeVM.uiState },
                                        "SearchViewModel" to { searchVM.uiState },
                                        "ProductDetailVM" to { productDetailVM.uiState },
                                        "CartViewModel" to { cartVM.uiState },
                                        "CheckoutViewModel" to { checkoutVM.uiState },
                                        "ProfileViewModel" to { profileVM.uiState },
                                        "ChatViewModel" to { chatVM.uiState },
                                        "OrderHistoryVM" to { orderHistoryVM.uiState },
                                        "SettingsViewModel" to { settingsVM.uiState },
                                        "NotificationsVM" to { notificationsVM.uiState },
                                        "OnboardingVM" to { onboardingVM.uiState },
                                        "ReviewsViewModel" to { reviewsVM.uiState },
                                        "WishlistViewModel" to { wishlistVM.uiState },
                                    )
                                    val r = HiltFullBenchmarkRunner.run(application, vmResolvers)
                                    result = r
                                    isRunning = false

                                    r.logDetailed("HiltFullBench")
                                }.start()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isRunning
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Running...")
                            } else {
                                Text("Run Full Benchmark")
                            }
                        }

                        if (result != null) {
                            val r = result!!

                            Spacer(Modifier.height(16.dp))

                            // Grand total card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Grand Total", style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold)
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("${r.grandTotalUs}us",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold)
                                            Text("(${r.grandTotalUs / 1000}ms)",
                                                style = MaterialTheme.typography.bodySmall)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("${r.totalClasses}",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold)
                                            Text("classes measured",
                                                style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Layer breakdown
                            for (layer in r.layers) {
                                LayerCard(layer)
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerCard(layer: LayerResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("${layer.name} (${layer.count})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                    Text(layer.description,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${layer.totalUs}us",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(8.dp))

            // Top 5 slowest
            val sorted = layer.items.sortedByDescending { it.second }
            val top = sorted.take(5)
            for ((name, nanos) in top) {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(name, fontSize = 11.sp)
                    Text("${nanos / 1000}us", fontSize = 11.sp,
                        fontWeight = if (nanos / 1000 > 100) FontWeight.Bold else FontWeight.Normal,
                        color = if (nanos / 1000 > 1000) Color.Red
                            else if (nanos / 1000 > 100) Color(0xFFFF9800)
                            else MaterialTheme.colorScheme.onSurface)
                }
            }

            if (sorted.size > 5) {
                Text("  ... +${sorted.size - 5} more (all <${sorted[5].second / 1000}us)",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
