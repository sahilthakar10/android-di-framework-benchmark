package com.codeint.benchmarking

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Benchmarks real @HiltViewModel creation via ViewModelProvider — the exact same
 * mechanism a real Activity uses. Each ViewModel is created by Hilt's
 * HiltViewModelFactory, which resolves the full dependency chain at creation time.
 *
 * Launch via: adb shell am start -n com.codeint.benchmarking/.HiltViewModelBenchmarkActivity
 */
@AndroidEntryPoint
class HiltViewModelBenchmarkActivity : ComponentActivity() {

    // Real Hilt ViewModel creation — same as any production Activity
    private val homeViewModel: HomeViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val productDetailViewModel: ProductDetailViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val orderHistoryViewModel: OrderHistoryViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val reviewsViewModel: ReviewsViewModel by viewModels()
    private val wishlistViewModel: WishlistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "Starting Hilt ViewModel benchmark...")

        lifecycleScope.launch(Dispatchers.Default) {
            val results = mutableMapOf<String, Long>()

            // Each access triggers lazy ViewModel creation via ViewModelProvider + HiltViewModelFactory
            // This is the REAL cost of creating a @HiltViewModel in a production app
            results["HomeViewModel"] = measureNanos { homeViewModel.uiState }
            results["SearchViewModel"] = measureNanos { searchViewModel.uiState }
            results["ProductDetailVM"] = measureNanos { productDetailViewModel.uiState }
            results["CartViewModel"] = measureNanos { cartViewModel.uiState }
            results["CheckoutViewModel"] = measureNanos { checkoutViewModel.uiState }
            results["ProfileViewModel"] = measureNanos { profileViewModel.uiState }
            results["ChatViewModel"] = measureNanos { chatViewModel.uiState }
            results["OrderHistoryVM"] = measureNanos { orderHistoryViewModel.uiState }
            results["SettingsViewModel"] = measureNanos { settingsViewModel.uiState }
            results["NotificationsVM"] = measureNanos { notificationsViewModel.uiState }
            results["OnboardingVM"] = measureNanos { onboardingViewModel.uiState }
            results["ReviewsViewModel"] = measureNanos { reviewsViewModel.uiState }
            results["WishlistViewModel"] = measureNanos { wishlistViewModel.uiState }

            Log.i(TAG, "")
            Log.i(TAG, "══════════════════════════════════════════")
            Log.i(TAG, "  HILT @HiltViewModel CREATION BENCHMARK")
            Log.i(TAG, "  Real ViewModelProvider, 13 ViewModels")
            Log.i(TAG, "══════════════════════════════════════════")
            Log.i(TAG, "")

            var totalNanos = 0L
            for ((name, nanos) in results.entries.sortedByDescending { it.value }) {
                val us = nanos / 1000
                Log.i(TAG, "  ${name.padEnd(22)} ${us}us")
                totalNanos += nanos
            }

            Log.i(TAG, "  ${"─".repeat(35)}")
            Log.i(TAG, "  Total: ${totalNanos / 1000}us (${totalNanos / 1_000_000}ms)")
            Log.i(TAG, "  Avg:   ${totalNanos / 1000 / results.size}us per ViewModel")
            Log.i(TAG, "")
            Log.i(TAG, "  Note: Each ViewModel created via ViewModelProvider")
            Log.i(TAG, "  + HiltViewModelFactory. Full dependency chain resolved")
            Log.i(TAG, "  by Dagger at creation time. Subsequent access is cached.")
            Log.i(TAG, "══════════════════════════════════════════")

            withContext(Dispatchers.Main) { finish() }
        }
    }

    private inline fun measureNanos(block: () -> Any?): Long {
        val start = System.nanoTime()
        block()
        return System.nanoTime() - start
    }

    companion object {
        private const val TAG = "HiltVMBenchmark"
    }
}
