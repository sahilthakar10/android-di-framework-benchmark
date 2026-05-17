package com.codeint.shopapp.hilt.di

// Core
import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.core.auth.*
import com.codeint.shopapp.hilt.core.analytics.*
import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.core.config.*
import com.codeint.shopapp.hilt.core.logging.*
import com.codeint.shopapp.hilt.core.image.*
import com.codeint.shopapp.hilt.core.notification.*
import com.codeint.shopapp.hilt.core.location.*

// Data
import com.codeint.shopapp.hilt.data.product.ProductRepository
import com.codeint.shopapp.hilt.data.product.remote.ProductRemoteDataSource
import com.codeint.shopapp.hilt.data.product.local.ProductLocalDataSource
import com.codeint.shopapp.hilt.data.product.mapper.ProductMapper
import com.codeint.shopapp.hilt.data.user.UserRepository
import com.codeint.shopapp.hilt.data.user.remote.UserRemoteDataSource
import com.codeint.shopapp.hilt.data.user.local.UserLocalDataSource
import com.codeint.shopapp.hilt.data.user.mapper.UserMapper
import com.codeint.shopapp.hilt.data.cart.CartRepository
import com.codeint.shopapp.hilt.data.cart.remote.CartRemoteDataSource
import com.codeint.shopapp.hilt.data.cart.local.CartLocalDataSource
import com.codeint.shopapp.hilt.data.cart.mapper.CartMapper
import com.codeint.shopapp.hilt.data.order.OrderRepository
import com.codeint.shopapp.hilt.data.order.remote.OrderRemoteDataSource
import com.codeint.shopapp.hilt.data.order.local.OrderLocalDataSource
import com.codeint.shopapp.hilt.data.order.mapper.OrderMapper
import com.codeint.shopapp.hilt.data.payment.PaymentRepository
import com.codeint.shopapp.hilt.data.payment.remote.PaymentRemoteDataSource
import com.codeint.shopapp.hilt.data.payment.local.PaymentLocalDataSource
import com.codeint.shopapp.hilt.data.payment.mapper.PaymentMapper
import com.codeint.shopapp.hilt.data.chat.ChatRepository
import com.codeint.shopapp.hilt.data.chat.remote.ChatRemoteDataSource
import com.codeint.shopapp.hilt.data.chat.local.ChatLocalDataSource
import com.codeint.shopapp.hilt.data.chat.mapper.ChatMapper
import com.codeint.shopapp.hilt.data.search.SearchRepository
import com.codeint.shopapp.hilt.data.search.remote.SearchRemoteDataSource
import com.codeint.shopapp.hilt.data.search.local.SearchLocalDataSource
import com.codeint.shopapp.hilt.data.search.mapper.SearchMapper
import com.codeint.shopapp.hilt.data.review.ReviewRepository
import com.codeint.shopapp.hilt.data.review.remote.ReviewRemoteDataSource
import com.codeint.shopapp.hilt.data.review.local.ReviewLocalDataSource
import com.codeint.shopapp.hilt.data.review.mapper.ReviewMapper
import com.codeint.shopapp.hilt.data.category.CategoryRepository
import com.codeint.shopapp.hilt.data.category.remote.CategoryRemoteDataSource
import com.codeint.shopapp.hilt.data.category.local.CategoryLocalDataSource
import com.codeint.shopapp.hilt.data.category.mapper.CategoryMapper
import com.codeint.shopapp.hilt.data.address.AddressRepository
import com.codeint.shopapp.hilt.data.address.remote.AddressRemoteDataSource
import com.codeint.shopapp.hilt.data.address.local.AddressLocalDataSource
import com.codeint.shopapp.hilt.data.address.mapper.AddressMapper
import com.codeint.shopapp.hilt.data.wishlist.WishlistRepository
import com.codeint.shopapp.hilt.data.wishlist.remote.WishlistRemoteDataSource
import com.codeint.shopapp.hilt.data.wishlist.local.WishlistLocalDataSource
import com.codeint.shopapp.hilt.data.wishlist.mapper.WishlistMapper
import com.codeint.shopapp.hilt.data.promotion.PromotionRepository
import com.codeint.shopapp.hilt.data.promotion.remote.PromotionRemoteDataSource
import com.codeint.shopapp.hilt.data.promotion.local.PromotionLocalDataSource
import com.codeint.shopapp.hilt.data.promotion.mapper.PromotionMapper
import com.codeint.shopapp.hilt.data.shipping.ShippingRepository
import com.codeint.shopapp.hilt.data.shipping.remote.ShippingRemoteDataSource
import com.codeint.shopapp.hilt.data.shipping.local.ShippingLocalDataSource
import com.codeint.shopapp.hilt.data.shipping.mapper.ShippingMapper
import com.codeint.shopapp.hilt.data.feed.FeedRepository
import com.codeint.shopapp.hilt.data.feed.remote.FeedRemoteDataSource
import com.codeint.shopapp.hilt.data.feed.local.FeedLocalDataSource
import com.codeint.shopapp.hilt.data.feed.mapper.FeedMapper

// Domain (sample use cases per domain)
import com.codeint.shopapp.hilt.domain.product.GetProductListUseCase
import com.codeint.shopapp.hilt.domain.product.GetProductDetailUseCase
import com.codeint.shopapp.hilt.domain.user.GetUserListUseCase
import com.codeint.shopapp.hilt.domain.user.GetUserDetailUseCase
import com.codeint.shopapp.hilt.domain.cart.GetCartListUseCase
import com.codeint.shopapp.hilt.domain.cart.GetCartDetailUseCase
import com.codeint.shopapp.hilt.domain.order.GetOrderListUseCase
import com.codeint.shopapp.hilt.domain.order.GetOrderDetailUseCase
import com.codeint.shopapp.hilt.domain.payment.GetPaymentListUseCase
import com.codeint.shopapp.hilt.domain.payment.GetPaymentDetailUseCase
import com.codeint.shopapp.hilt.domain.chat.GetChatListUseCase
import com.codeint.shopapp.hilt.domain.chat.GetChatDetailUseCase
import com.codeint.shopapp.hilt.domain.search.GetSearchListUseCase
import com.codeint.shopapp.hilt.domain.search.GetSearchDetailUseCase
import com.codeint.shopapp.hilt.domain.review.GetReviewListUseCase
import com.codeint.shopapp.hilt.domain.review.GetReviewDetailUseCase
import com.codeint.shopapp.hilt.domain.category.GetCategoryListUseCase
import com.codeint.shopapp.hilt.domain.category.GetCategoryDetailUseCase
import com.codeint.shopapp.hilt.domain.address.GetAddressListUseCase
import com.codeint.shopapp.hilt.domain.address.GetAddressDetailUseCase
import com.codeint.shopapp.hilt.domain.wishlist.GetWishlistListUseCase
import com.codeint.shopapp.hilt.domain.wishlist.GetWishlistDetailUseCase
import com.codeint.shopapp.hilt.domain.promotion.GetPromotionListUseCase
import com.codeint.shopapp.hilt.domain.promotion.GetPromotionDetailUseCase
import com.codeint.shopapp.hilt.domain.shipping.GetShippingListUseCase
import com.codeint.shopapp.hilt.domain.shipping.GetShippingDetailUseCase
import com.codeint.shopapp.hilt.domain.feed.GetFeedListUseCase
import com.codeint.shopapp.hilt.domain.feed.GetFeedDetailUseCase

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BenchmarkEntryPoint {

    // ── Core Singletons ──
    fun httpClient(): HttpClient
    fun authManager(): AuthManager
    fun tokenStorage(): TokenStorage
    fun sessionManager(): SessionManager
    fun analyticsTracker(): AnalyticsTracker
    fun crashReporter(): CrashReporter
    fun databaseManager(): DatabaseManager
    fun preferencesManager(): PreferencesManager
    fun secureStorage(): SecureStorage
    fun cacheManager(): CacheManager
    fun appLogger(): AppLogger
    fun tokenProvider(): TokenProvider
    fun networkLogger(): NetworkLogger
    fun cachePolicy(): CachePolicy

    // ── Core Services (non-singleton) ──
    fun authInterceptor(): AuthInterceptor
    fun rateLimiter(): RateLimiter
    fun webSocketManager(): WebSocketManager
    fun graphQLClient(): GraphQLClient
    fun imageLoader(): ImageLoader
    fun featureFlagManager(): FeatureFlagManager
    fun appConfigProvider(): AppConfigProvider
    fun notificationManager(): NotificationManager
    fun deepLinkHandler(): DeepLinkHandler
    fun locationManager(): LocationManager
    fun storeLocator(): StoreLocator
    fun auditLogger(): AuditLogger
    fun eventBus(): EventBus
    fun remoteConfigManager(): RemoteConfigManager

    // ── Repositories (14 domains) ──
    fun productRepository(): ProductRepository
    fun userRepository(): UserRepository
    fun cartRepository(): CartRepository
    fun orderRepository(): OrderRepository
    fun paymentRepository(): PaymentRepository
    fun chatRepository(): ChatRepository
    fun searchRepository(): SearchRepository
    fun reviewRepository(): ReviewRepository
    fun categoryRepository(): CategoryRepository
    fun addressRepository(): AddressRepository
    fun wishlistRepository(): WishlistRepository
    fun promotionRepository(): PromotionRepository
    fun shippingRepository(): ShippingRepository
    fun feedRepository(): FeedRepository

    // ── RemoteDataSources (14 domains) ──
    fun productRemoteDataSource(): ProductRemoteDataSource
    fun userRemoteDataSource(): UserRemoteDataSource
    fun cartRemoteDataSource(): CartRemoteDataSource
    fun orderRemoteDataSource(): OrderRemoteDataSource
    fun paymentRemoteDataSource(): PaymentRemoteDataSource
    fun chatRemoteDataSource(): ChatRemoteDataSource
    fun searchRemoteDataSource(): SearchRemoteDataSource
    fun reviewRemoteDataSource(): ReviewRemoteDataSource
    fun categoryRemoteDataSource(): CategoryRemoteDataSource
    fun addressRemoteDataSource(): AddressRemoteDataSource
    fun wishlistRemoteDataSource(): WishlistRemoteDataSource
    fun promotionRemoteDataSource(): PromotionRemoteDataSource
    fun shippingRemoteDataSource(): ShippingRemoteDataSource
    fun feedRemoteDataSource(): FeedRemoteDataSource

    // ── LocalDataSources (14 domains) ──
    fun productLocalDataSource(): ProductLocalDataSource
    fun userLocalDataSource(): UserLocalDataSource
    fun cartLocalDataSource(): CartLocalDataSource
    fun orderLocalDataSource(): OrderLocalDataSource
    fun paymentLocalDataSource(): PaymentLocalDataSource
    fun chatLocalDataSource(): ChatLocalDataSource
    fun searchLocalDataSource(): SearchLocalDataSource
    fun reviewLocalDataSource(): ReviewLocalDataSource
    fun categoryLocalDataSource(): CategoryLocalDataSource
    fun addressLocalDataSource(): AddressLocalDataSource
    fun wishlistLocalDataSource(): WishlistLocalDataSource
    fun promotionLocalDataSource(): PromotionLocalDataSource
    fun shippingLocalDataSource(): ShippingLocalDataSource
    fun feedLocalDataSource(): FeedLocalDataSource

    // ── Mappers (14 domains) ──
    fun productMapper(): ProductMapper
    fun userMapper(): UserMapper
    fun cartMapper(): CartMapper
    fun orderMapper(): OrderMapper
    fun paymentMapper(): PaymentMapper
    fun chatMapper(): ChatMapper
    fun searchMapper(): SearchMapper
    fun reviewMapper(): ReviewMapper
    fun categoryMapper(): CategoryMapper
    fun addressMapper(): AddressMapper
    fun wishlistMapper(): WishlistMapper
    fun promotionMapper(): PromotionMapper
    fun shippingMapper(): ShippingMapper
    fun feedMapper(): FeedMapper

    // ── UseCases (2 per domain = 28) ──
    fun getProductListUseCase(): GetProductListUseCase
    fun getProductDetailUseCase(): GetProductDetailUseCase
    fun getUserListUseCase(): GetUserListUseCase
    fun getUserDetailUseCase(): GetUserDetailUseCase
    fun getCartListUseCase(): GetCartListUseCase
    fun getCartDetailUseCase(): GetCartDetailUseCase
    fun getOrderListUseCase(): GetOrderListUseCase
    fun getOrderDetailUseCase(): GetOrderDetailUseCase
    fun getPaymentListUseCase(): GetPaymentListUseCase
    fun getPaymentDetailUseCase(): GetPaymentDetailUseCase
    fun getChatListUseCase(): GetChatListUseCase
    fun getChatDetailUseCase(): GetChatDetailUseCase
    fun getSearchListUseCase(): GetSearchListUseCase
    fun getSearchDetailUseCase(): GetSearchDetailUseCase
    fun getReviewListUseCase(): GetReviewListUseCase
    fun getReviewDetailUseCase(): GetReviewDetailUseCase
    fun getCategoryListUseCase(): GetCategoryListUseCase
    fun getCategoryDetailUseCase(): GetCategoryDetailUseCase
    fun getAddressListUseCase(): GetAddressListUseCase
    fun getAddressDetailUseCase(): GetAddressDetailUseCase
    fun getWishlistListUseCase(): GetWishlistListUseCase
    fun getWishlistDetailUseCase(): GetWishlistDetailUseCase
    fun getPromotionListUseCase(): GetPromotionListUseCase
    fun getPromotionDetailUseCase(): GetPromotionDetailUseCase
    fun getShippingListUseCase(): GetShippingListUseCase
    fun getShippingDetailUseCase(): GetShippingDetailUseCase
    fun getFeedListUseCase(): GetFeedListUseCase
    fun getFeedDetailUseCase(): GetFeedDetailUseCase

}