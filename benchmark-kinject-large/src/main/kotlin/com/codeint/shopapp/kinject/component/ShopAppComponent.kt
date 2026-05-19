package com.codeint.shopapp.kinject.component

import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import com.codeint.shopapp.kinject.core.network.*
import com.codeint.shopapp.kinject.core.auth.*
import com.codeint.shopapp.kinject.core.analytics.*
import com.codeint.shopapp.kinject.core.storage.*
import com.codeint.shopapp.kinject.core.config.*
import com.codeint.shopapp.kinject.core.logging.*
import com.codeint.shopapp.kinject.core.image.*
import com.codeint.shopapp.kinject.core.notification.*
import com.codeint.shopapp.kinject.core.location.*
import com.codeint.shopapp.kinject.data.product.ProductRepository
import com.codeint.shopapp.kinject.data.product.OfflineFirstProductRepository
import com.codeint.shopapp.kinject.data.product.remote.ProductRemoteDataSource
import com.codeint.shopapp.kinject.data.product.local.ProductLocalDataSource
import com.codeint.shopapp.kinject.data.product.mapper.ProductMapper
import com.codeint.shopapp.kinject.domain.product.*
import com.codeint.shopapp.kinject.data.user.UserRepository
import com.codeint.shopapp.kinject.data.user.OfflineFirstUserRepository
import com.codeint.shopapp.kinject.data.user.remote.UserRemoteDataSource
import com.codeint.shopapp.kinject.data.user.local.UserLocalDataSource
import com.codeint.shopapp.kinject.data.user.mapper.UserMapper
import com.codeint.shopapp.kinject.domain.user.*
import com.codeint.shopapp.kinject.data.cart.CartRepository
import com.codeint.shopapp.kinject.data.cart.OfflineFirstCartRepository
import com.codeint.shopapp.kinject.data.cart.remote.CartRemoteDataSource
import com.codeint.shopapp.kinject.data.cart.local.CartLocalDataSource
import com.codeint.shopapp.kinject.data.cart.mapper.CartMapper
import com.codeint.shopapp.kinject.domain.cart.*
import com.codeint.shopapp.kinject.data.order.OrderRepository
import com.codeint.shopapp.kinject.data.order.OfflineFirstOrderRepository
import com.codeint.shopapp.kinject.data.order.remote.OrderRemoteDataSource
import com.codeint.shopapp.kinject.data.order.local.OrderLocalDataSource
import com.codeint.shopapp.kinject.data.order.mapper.OrderMapper
import com.codeint.shopapp.kinject.domain.order.*
import com.codeint.shopapp.kinject.data.payment.PaymentRepository
import com.codeint.shopapp.kinject.data.payment.OfflineFirstPaymentRepository
import com.codeint.shopapp.kinject.data.payment.remote.PaymentRemoteDataSource
import com.codeint.shopapp.kinject.data.payment.local.PaymentLocalDataSource
import com.codeint.shopapp.kinject.data.payment.mapper.PaymentMapper
import com.codeint.shopapp.kinject.domain.payment.*
import com.codeint.shopapp.kinject.data.chat.ChatRepository
import com.codeint.shopapp.kinject.data.chat.OfflineFirstChatRepository
import com.codeint.shopapp.kinject.data.chat.remote.ChatRemoteDataSource
import com.codeint.shopapp.kinject.data.chat.local.ChatLocalDataSource
import com.codeint.shopapp.kinject.data.chat.mapper.ChatMapper
import com.codeint.shopapp.kinject.domain.chat.*
import com.codeint.shopapp.kinject.data.search.SearchRepository
import com.codeint.shopapp.kinject.data.search.OfflineFirstSearchRepository
import com.codeint.shopapp.kinject.data.search.remote.SearchRemoteDataSource
import com.codeint.shopapp.kinject.data.search.local.SearchLocalDataSource
import com.codeint.shopapp.kinject.data.search.mapper.SearchMapper
import com.codeint.shopapp.kinject.domain.search.*
import com.codeint.shopapp.kinject.data.review.ReviewRepository
import com.codeint.shopapp.kinject.data.review.OfflineFirstReviewRepository
import com.codeint.shopapp.kinject.data.review.remote.ReviewRemoteDataSource
import com.codeint.shopapp.kinject.data.review.local.ReviewLocalDataSource
import com.codeint.shopapp.kinject.data.review.mapper.ReviewMapper
import com.codeint.shopapp.kinject.domain.review.*
import com.codeint.shopapp.kinject.data.category.CategoryRepository
import com.codeint.shopapp.kinject.data.category.OfflineFirstCategoryRepository
import com.codeint.shopapp.kinject.data.category.remote.CategoryRemoteDataSource
import com.codeint.shopapp.kinject.data.category.local.CategoryLocalDataSource
import com.codeint.shopapp.kinject.data.category.mapper.CategoryMapper
import com.codeint.shopapp.kinject.domain.category.*
import com.codeint.shopapp.kinject.data.address.AddressRepository
import com.codeint.shopapp.kinject.data.address.OfflineFirstAddressRepository
import com.codeint.shopapp.kinject.data.address.remote.AddressRemoteDataSource
import com.codeint.shopapp.kinject.data.address.local.AddressLocalDataSource
import com.codeint.shopapp.kinject.data.address.mapper.AddressMapper
import com.codeint.shopapp.kinject.domain.address.*
import com.codeint.shopapp.kinject.data.wishlist.WishlistRepository
import com.codeint.shopapp.kinject.data.wishlist.OfflineFirstWishlistRepository
import com.codeint.shopapp.kinject.data.wishlist.remote.WishlistRemoteDataSource
import com.codeint.shopapp.kinject.data.wishlist.local.WishlistLocalDataSource
import com.codeint.shopapp.kinject.data.wishlist.mapper.WishlistMapper
import com.codeint.shopapp.kinject.domain.wishlist.*
import com.codeint.shopapp.kinject.data.promotion.PromotionRepository
import com.codeint.shopapp.kinject.data.promotion.OfflineFirstPromotionRepository
import com.codeint.shopapp.kinject.data.promotion.remote.PromotionRemoteDataSource
import com.codeint.shopapp.kinject.data.promotion.local.PromotionLocalDataSource
import com.codeint.shopapp.kinject.data.promotion.mapper.PromotionMapper
import com.codeint.shopapp.kinject.domain.promotion.*
import com.codeint.shopapp.kinject.data.shipping.ShippingRepository
import com.codeint.shopapp.kinject.data.shipping.OfflineFirstShippingRepository
import com.codeint.shopapp.kinject.data.shipping.remote.ShippingRemoteDataSource
import com.codeint.shopapp.kinject.data.shipping.local.ShippingLocalDataSource
import com.codeint.shopapp.kinject.data.shipping.mapper.ShippingMapper
import com.codeint.shopapp.kinject.domain.shipping.*
import com.codeint.shopapp.kinject.data.feed.FeedRepository
import com.codeint.shopapp.kinject.data.feed.OfflineFirstFeedRepository
import com.codeint.shopapp.kinject.data.feed.remote.FeedRemoteDataSource
import com.codeint.shopapp.kinject.data.feed.local.FeedLocalDataSource
import com.codeint.shopapp.kinject.data.feed.mapper.FeedMapper
import com.codeint.shopapp.kinject.domain.feed.*
import com.codeint.shopapp.kinject.feature.home.HomeViewModel
import com.codeint.shopapp.kinject.feature.search.SearchViewModel
import com.codeint.shopapp.kinject.feature.productdetail.ProductDetailViewModel
import com.codeint.shopapp.kinject.feature.cart.CartViewModel
import com.codeint.shopapp.kinject.feature.checkout.CheckoutViewModel
import com.codeint.shopapp.kinject.feature.profile.ProfileViewModel
import com.codeint.shopapp.kinject.feature.chat.ChatViewModel
import com.codeint.shopapp.kinject.feature.orders.OrderHistoryViewModel
import com.codeint.shopapp.kinject.feature.settings.SettingsViewModel
import com.codeint.shopapp.kinject.feature.notifications.NotificationsViewModel
import com.codeint.shopapp.kinject.feature.onboarding.OnboardingViewModel
import com.codeint.shopapp.kinject.feature.reviews.ReviewsViewModel
import com.codeint.shopapp.kinject.feature.wishlist.WishlistViewModel

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class ShopAppComponent {

    // Config objects via @Provides
    @Provides fun provideHttpClient(): HttpClient = HttpClient("https://api.shopapp.com", 30_000)
    @Provides fun provideApiResponseParser(): ApiResponseParser = ApiResponseParser()
    @Provides fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor()
    @Provides fun provideRetryPolicy(): RetryPolicy = RetryPolicy(3, 1000)
    @Provides fun provideSslPinningConfig(): SslPinningConfig = SslPinningConfig(listOf("sha256/abc123"))

    // ── Singleton Accessors ──
    abstract val httpClient: HttpClient
    abstract val apiResponseParser: ApiResponseParser
    abstract val networkMonitor: NetworkMonitor
    abstract val retryPolicy: RetryPolicy
    abstract val sslPinningConfig: SslPinningConfig
    abstract val tokenProvider: TokenProvider
    abstract val networkLogger: NetworkLogger
    abstract val cachePolicy: CachePolicy
    abstract val authManager: AuthManager
    abstract val tokenStorage: TokenStorage
    abstract val sessionManager: SessionManager
    abstract val analyticsTracker: AnalyticsTracker
    abstract val crashReporter: CrashReporter
    abstract val databaseManager: DatabaseManager
    abstract val preferencesManager: PreferencesManager
    abstract val secureStorage: SecureStorage
    abstract val cacheManager: CacheManager
    abstract val appLogger: AppLogger

    // ── ViewModel Accessors ──
    abstract val homeViewModel: HomeViewModel
    abstract val searchViewModel: SearchViewModel
    abstract val productDetailViewModel: ProductDetailViewModel
    abstract val cartViewModel: CartViewModel
    abstract val checkoutViewModel: CheckoutViewModel
    abstract val profileViewModel: ProfileViewModel
    abstract val chatViewModel: ChatViewModel
    abstract val orderHistoryViewModel: OrderHistoryViewModel
    abstract val settingsViewModel: SettingsViewModel
    abstract val notificationsViewModel: NotificationsViewModel
    abstract val onboardingViewModel: OnboardingViewModel
    abstract val reviewsViewModel: ReviewsViewModel
    abstract val wishlistViewModel: WishlistViewModel

    abstract val authInterceptor: AuthInterceptor
    abstract val rateLimiter: RateLimiter
    abstract val webSocketManager: WebSocketManager
    abstract val graphQLClient: GraphQLClient
    abstract val imageLoader: ImageLoader
    abstract val featureFlagManager: FeatureFlagManager
    abstract val appConfigProvider: AppConfigProvider
    abstract val notificationManager: NotificationManager
    abstract val deepLinkHandler: DeepLinkHandler
    abstract val locationManager: LocationManager
    abstract val storeLocator: StoreLocator
    abstract val auditLogger: AuditLogger
    abstract val eventBus: EventBus
    abstract val remoteConfigManager: RemoteConfigManager
    abstract val productRepository: ProductRepository
    abstract val productRemoteDataSource: ProductRemoteDataSource
    abstract val productLocalDataSource: ProductLocalDataSource
    abstract val productMapper: ProductMapper
    abstract val getProductListUseCase: GetProductListUseCase
    abstract val getProductDetailUseCase: GetProductDetailUseCase
    abstract val userRepository: UserRepository
    abstract val userRemoteDataSource: UserRemoteDataSource
    abstract val userLocalDataSource: UserLocalDataSource
    abstract val userMapper: UserMapper
    abstract val getUserListUseCase: GetUserListUseCase
    abstract val getUserDetailUseCase: GetUserDetailUseCase
    abstract val cartRepository: CartRepository
    abstract val cartRemoteDataSource: CartRemoteDataSource
    abstract val cartLocalDataSource: CartLocalDataSource
    abstract val cartMapper: CartMapper
    abstract val getCartListUseCase: GetCartListUseCase
    abstract val getCartDetailUseCase: GetCartDetailUseCase
    abstract val orderRepository: OrderRepository
    abstract val orderRemoteDataSource: OrderRemoteDataSource
    abstract val orderLocalDataSource: OrderLocalDataSource
    abstract val orderMapper: OrderMapper
    abstract val getOrderListUseCase: GetOrderListUseCase
    abstract val getOrderDetailUseCase: GetOrderDetailUseCase
    abstract val paymentRepository: PaymentRepository
    abstract val paymentRemoteDataSource: PaymentRemoteDataSource
    abstract val paymentLocalDataSource: PaymentLocalDataSource
    abstract val paymentMapper: PaymentMapper
    abstract val getPaymentListUseCase: GetPaymentListUseCase
    abstract val getPaymentDetailUseCase: GetPaymentDetailUseCase
    abstract val chatRepository: ChatRepository
    abstract val chatRemoteDataSource: ChatRemoteDataSource
    abstract val chatLocalDataSource: ChatLocalDataSource
    abstract val chatMapper: ChatMapper
    abstract val getChatListUseCase: GetChatListUseCase
    abstract val getChatDetailUseCase: GetChatDetailUseCase
    abstract val searchRepository: SearchRepository
    abstract val searchRemoteDataSource: SearchRemoteDataSource
    abstract val searchLocalDataSource: SearchLocalDataSource
    abstract val searchMapper: SearchMapper
    abstract val getSearchListUseCase: GetSearchListUseCase
    abstract val getSearchDetailUseCase: GetSearchDetailUseCase
    abstract val reviewRepository: ReviewRepository
    abstract val reviewRemoteDataSource: ReviewRemoteDataSource
    abstract val reviewLocalDataSource: ReviewLocalDataSource
    abstract val reviewMapper: ReviewMapper
    abstract val getReviewListUseCase: GetReviewListUseCase
    abstract val getReviewDetailUseCase: GetReviewDetailUseCase
    abstract val categoryRepository: CategoryRepository
    abstract val categoryRemoteDataSource: CategoryRemoteDataSource
    abstract val categoryLocalDataSource: CategoryLocalDataSource
    abstract val categoryMapper: CategoryMapper
    abstract val getCategoryListUseCase: GetCategoryListUseCase
    abstract val getCategoryDetailUseCase: GetCategoryDetailUseCase
    abstract val addressRepository: AddressRepository
    abstract val addressRemoteDataSource: AddressRemoteDataSource
    abstract val addressLocalDataSource: AddressLocalDataSource
    abstract val addressMapper: AddressMapper
    abstract val getAddressListUseCase: GetAddressListUseCase
    abstract val getAddressDetailUseCase: GetAddressDetailUseCase
    abstract val wishlistRepository: WishlistRepository
    abstract val wishlistRemoteDataSource: WishlistRemoteDataSource
    abstract val wishlistLocalDataSource: WishlistLocalDataSource
    abstract val wishlistMapper: WishlistMapper
    abstract val getWishlistListUseCase: GetWishlistListUseCase
    abstract val getWishlistDetailUseCase: GetWishlistDetailUseCase
    abstract val promotionRepository: PromotionRepository
    abstract val promotionRemoteDataSource: PromotionRemoteDataSource
    abstract val promotionLocalDataSource: PromotionLocalDataSource
    abstract val promotionMapper: PromotionMapper
    abstract val getPromotionListUseCase: GetPromotionListUseCase
    abstract val getPromotionDetailUseCase: GetPromotionDetailUseCase
    abstract val shippingRepository: ShippingRepository
    abstract val shippingRemoteDataSource: ShippingRemoteDataSource
    abstract val shippingLocalDataSource: ShippingLocalDataSource
    abstract val shippingMapper: ShippingMapper
    abstract val getShippingListUseCase: GetShippingListUseCase
    abstract val getShippingDetailUseCase: GetShippingDetailUseCase
    abstract val feedRepository: FeedRepository
    abstract val feedRemoteDataSource: FeedRemoteDataSource
    abstract val feedLocalDataSource: FeedLocalDataSource
    abstract val feedMapper: FeedMapper
    abstract val getFeedListUseCase: GetFeedListUseCase
    abstract val getFeedDetailUseCase: GetFeedDetailUseCase

    companion object
}
