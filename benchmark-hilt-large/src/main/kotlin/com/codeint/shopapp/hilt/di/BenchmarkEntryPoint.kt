package com.codeint.shopapp.hilt.di

import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.data.product.ProductRepository
import com.codeint.shopapp.hilt.feature.cart.CartViewModel
import com.codeint.shopapp.hilt.feature.chat.ChatViewModel
import com.codeint.shopapp.hilt.feature.checkout.CheckoutViewModel
import com.codeint.shopapp.hilt.feature.home.HomeViewModel
import com.codeint.shopapp.hilt.feature.orders.OrderHistoryViewModel
import com.codeint.shopapp.hilt.feature.productdetail.ProductDetailViewModel
import com.codeint.shopapp.hilt.feature.profile.ProfileViewModel
import com.codeint.shopapp.hilt.feature.search.SearchViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * EntryPoint to programmatically access Hilt-managed dependencies
 * for runtime benchmarking alongside Metro and Koin.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface BenchmarkEntryPoint {
    fun homeViewModel(): HomeViewModel
    fun searchViewModel(): SearchViewModel
    fun productDetailViewModel(): ProductDetailViewModel
    fun cartViewModel(): CartViewModel
    fun checkoutViewModel(): CheckoutViewModel
    fun profileViewModel(): ProfileViewModel
    fun chatViewModel(): ChatViewModel
    fun orderHistoryViewModel(): OrderHistoryViewModel
    fun analyticsTracker(): AnalyticsTracker
    fun productRepository(): ProductRepository
}
