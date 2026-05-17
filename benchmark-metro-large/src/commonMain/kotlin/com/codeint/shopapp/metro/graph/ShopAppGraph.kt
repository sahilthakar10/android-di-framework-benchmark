package com.codeint.shopapp.metro.graph

import dev.zacsweers.metro.*
import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.core.auth.*
import com.codeint.shopapp.metro.core.analytics.*
import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.core.config.*
import com.codeint.shopapp.metro.core.logging.*
import com.codeint.shopapp.metro.core.image.*
import com.codeint.shopapp.metro.core.notification.*
import com.codeint.shopapp.metro.core.location.*
import com.codeint.shopapp.metro.data.product.ProductRepository
import com.codeint.shopapp.metro.data.product.OfflineFirstProductRepository
import com.codeint.shopapp.metro.data.product.remote.ProductRemoteDataSource
import com.codeint.shopapp.metro.data.product.local.ProductLocalDataSource
import com.codeint.shopapp.metro.data.product.mapper.ProductMapper
import com.codeint.shopapp.metro.domain.product.*
import com.codeint.shopapp.metro.data.user.UserRepository
import com.codeint.shopapp.metro.data.user.OfflineFirstUserRepository
import com.codeint.shopapp.metro.data.user.remote.UserRemoteDataSource
import com.codeint.shopapp.metro.data.user.local.UserLocalDataSource
import com.codeint.shopapp.metro.data.user.mapper.UserMapper
import com.codeint.shopapp.metro.domain.user.*
import com.codeint.shopapp.metro.data.cart.CartRepository
import com.codeint.shopapp.metro.data.cart.OfflineFirstCartRepository
import com.codeint.shopapp.metro.data.cart.remote.CartRemoteDataSource
import com.codeint.shopapp.metro.data.cart.local.CartLocalDataSource
import com.codeint.shopapp.metro.data.cart.mapper.CartMapper
import com.codeint.shopapp.metro.domain.cart.*
import com.codeint.shopapp.metro.data.order.OrderRepository
import com.codeint.shopapp.metro.data.order.OfflineFirstOrderRepository
import com.codeint.shopapp.metro.data.order.remote.OrderRemoteDataSource
import com.codeint.shopapp.metro.data.order.local.OrderLocalDataSource
import com.codeint.shopapp.metro.data.order.mapper.OrderMapper
import com.codeint.shopapp.metro.domain.order.*
import com.codeint.shopapp.metro.data.payment.PaymentRepository
import com.codeint.shopapp.metro.data.payment.OfflineFirstPaymentRepository
import com.codeint.shopapp.metro.data.payment.remote.PaymentRemoteDataSource
import com.codeint.shopapp.metro.data.payment.local.PaymentLocalDataSource
import com.codeint.shopapp.metro.data.payment.mapper.PaymentMapper
import com.codeint.shopapp.metro.domain.payment.*
import com.codeint.shopapp.metro.data.chat.ChatRepository
import com.codeint.shopapp.metro.data.chat.OfflineFirstChatRepository
import com.codeint.shopapp.metro.data.chat.remote.ChatRemoteDataSource
import com.codeint.shopapp.metro.data.chat.local.ChatLocalDataSource
import com.codeint.shopapp.metro.data.chat.mapper.ChatMapper
import com.codeint.shopapp.metro.domain.chat.*
import com.codeint.shopapp.metro.data.search.SearchRepository
import com.codeint.shopapp.metro.data.search.OfflineFirstSearchRepository
import com.codeint.shopapp.metro.data.search.remote.SearchRemoteDataSource
import com.codeint.shopapp.metro.data.search.local.SearchLocalDataSource
import com.codeint.shopapp.metro.data.search.mapper.SearchMapper
import com.codeint.shopapp.metro.domain.search.*
import com.codeint.shopapp.metro.data.review.ReviewRepository
import com.codeint.shopapp.metro.data.review.OfflineFirstReviewRepository
import com.codeint.shopapp.metro.data.review.remote.ReviewRemoteDataSource
import com.codeint.shopapp.metro.data.review.local.ReviewLocalDataSource
import com.codeint.shopapp.metro.data.review.mapper.ReviewMapper
import com.codeint.shopapp.metro.domain.review.*
import com.codeint.shopapp.metro.data.category.CategoryRepository
import com.codeint.shopapp.metro.data.category.OfflineFirstCategoryRepository
import com.codeint.shopapp.metro.data.category.remote.CategoryRemoteDataSource
import com.codeint.shopapp.metro.data.category.local.CategoryLocalDataSource
import com.codeint.shopapp.metro.data.category.mapper.CategoryMapper
import com.codeint.shopapp.metro.domain.category.*
import com.codeint.shopapp.metro.data.address.AddressRepository
import com.codeint.shopapp.metro.data.address.OfflineFirstAddressRepository
import com.codeint.shopapp.metro.data.address.remote.AddressRemoteDataSource
import com.codeint.shopapp.metro.data.address.local.AddressLocalDataSource
import com.codeint.shopapp.metro.data.address.mapper.AddressMapper
import com.codeint.shopapp.metro.domain.address.*
import com.codeint.shopapp.metro.data.wishlist.WishlistRepository
import com.codeint.shopapp.metro.data.wishlist.OfflineFirstWishlistRepository
import com.codeint.shopapp.metro.data.wishlist.remote.WishlistRemoteDataSource
import com.codeint.shopapp.metro.data.wishlist.local.WishlistLocalDataSource
import com.codeint.shopapp.metro.data.wishlist.mapper.WishlistMapper
import com.codeint.shopapp.metro.domain.wishlist.*
import com.codeint.shopapp.metro.data.promotion.PromotionRepository
import com.codeint.shopapp.metro.data.promotion.OfflineFirstPromotionRepository
import com.codeint.shopapp.metro.data.promotion.remote.PromotionRemoteDataSource
import com.codeint.shopapp.metro.data.promotion.local.PromotionLocalDataSource
import com.codeint.shopapp.metro.data.promotion.mapper.PromotionMapper
import com.codeint.shopapp.metro.domain.promotion.*
import com.codeint.shopapp.metro.data.shipping.ShippingRepository
import com.codeint.shopapp.metro.data.shipping.OfflineFirstShippingRepository
import com.codeint.shopapp.metro.data.shipping.remote.ShippingRemoteDataSource
import com.codeint.shopapp.metro.data.shipping.local.ShippingLocalDataSource
import com.codeint.shopapp.metro.data.shipping.mapper.ShippingMapper
import com.codeint.shopapp.metro.domain.shipping.*
import com.codeint.shopapp.metro.data.feed.FeedRepository
import com.codeint.shopapp.metro.data.feed.OfflineFirstFeedRepository
import com.codeint.shopapp.metro.data.feed.remote.FeedRemoteDataSource
import com.codeint.shopapp.metro.data.feed.local.FeedLocalDataSource
import com.codeint.shopapp.metro.data.feed.mapper.FeedMapper
import com.codeint.shopapp.metro.domain.feed.*
import com.codeint.shopapp.metro.feature.home.HomeViewModel
import com.codeint.shopapp.metro.feature.search.SearchViewModel
import com.codeint.shopapp.metro.feature.productdetail.ProductDetailViewModel
import com.codeint.shopapp.metro.feature.cart.CartViewModel
import com.codeint.shopapp.metro.feature.checkout.CheckoutViewModel
import com.codeint.shopapp.metro.feature.profile.ProfileViewModel
import com.codeint.shopapp.metro.feature.chat.ChatViewModel
import com.codeint.shopapp.metro.feature.orders.OrderHistoryViewModel
import com.codeint.shopapp.metro.feature.settings.SettingsViewModel
import com.codeint.shopapp.metro.feature.notifications.NotificationsViewModel
import com.codeint.shopapp.metro.feature.onboarding.OnboardingViewModel
import com.codeint.shopapp.metro.feature.reviews.ReviewsViewModel
import com.codeint.shopapp.metro.feature.wishlist.WishlistViewModel

@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
interface ShopAppGraph {

    // Config objects via @Provides
    @Provides fun provideHttpClient(): HttpClient = HttpClient("https://api.shopapp.com", 30_000)
    @Provides fun provideApiResponseParser(): ApiResponseParser = ApiResponseParser()
    @Provides fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor()
    @Provides fun provideRetryPolicy(): RetryPolicy = RetryPolicy(3, 1000)
    @Provides fun provideSslPinningConfig(): SslPinningConfig = SslPinningConfig(listOf("sha256/abc123"))

    // Interface bindings via @Provides
    @Provides fun bindTokenProvider(impl: RealTokenProvider): TokenProvider = impl
    @Provides fun bindNetworkLogger(impl: RealNetworkLogger): NetworkLogger = impl
    @Provides fun bindCachePolicy(impl: RealCachePolicy): CachePolicy = impl
    @Provides fun bindAuthManager(impl: RealAuthManager): AuthManager = impl
    @Provides fun bindTokenStorage(impl: RealTokenStorage): TokenStorage = impl
    @Provides fun bindSessionManager(impl: RealSessionManager): SessionManager = impl
    @Provides fun bindAnalyticsTracker(impl: RealAnalyticsTracker): AnalyticsTracker = impl
    @Provides fun bindCrashReporter(impl: RealCrashReporter): CrashReporter = impl
    @Provides fun bindDatabaseManager(impl: RealDatabaseManager): DatabaseManager = impl
    @Provides fun bindPreferencesManager(impl: RealPreferencesManager): PreferencesManager = impl
    @Provides fun bindSecureStorage(impl: RealSecureStorage): SecureStorage = impl
    @Provides fun bindCacheManager(impl: RealCacheManager): CacheManager = impl
    @Provides fun bindAppLogger(impl: RealAppLogger): AppLogger = impl
    @Provides fun bindProductRepository(impl: OfflineFirstProductRepository): ProductRepository = impl
    @Provides fun bindUserRepository(impl: OfflineFirstUserRepository): UserRepository = impl
    @Provides fun bindCartRepository(impl: OfflineFirstCartRepository): CartRepository = impl
    @Provides fun bindOrderRepository(impl: OfflineFirstOrderRepository): OrderRepository = impl
    @Provides fun bindPaymentRepository(impl: OfflineFirstPaymentRepository): PaymentRepository = impl
    @Provides fun bindChatRepository(impl: OfflineFirstChatRepository): ChatRepository = impl
    @Provides fun bindSearchRepository(impl: OfflineFirstSearchRepository): SearchRepository = impl
    @Provides fun bindReviewRepository(impl: OfflineFirstReviewRepository): ReviewRepository = impl
    @Provides fun bindCategoryRepository(impl: OfflineFirstCategoryRepository): CategoryRepository = impl
    @Provides fun bindAddressRepository(impl: OfflineFirstAddressRepository): AddressRepository = impl
    @Provides fun bindWishlistRepository(impl: OfflineFirstWishlistRepository): WishlistRepository = impl
    @Provides fun bindPromotionRepository(impl: OfflineFirstPromotionRepository): PromotionRepository = impl
    @Provides fun bindShippingRepository(impl: OfflineFirstShippingRepository): ShippingRepository = impl
    @Provides fun bindFeedRepository(impl: OfflineFirstFeedRepository): FeedRepository = impl

    // ── Singleton Accessors ──
    val httpClient: HttpClient
    val apiResponseParser: ApiResponseParser
    val networkMonitor: NetworkMonitor
    val retryPolicy: RetryPolicy
    val sslPinningConfig: SslPinningConfig
    val tokenProvider: TokenProvider
    val networkLogger: NetworkLogger
    val cachePolicy: CachePolicy
    val authManager: AuthManager
    val tokenStorage: TokenStorage
    val sessionManager: SessionManager
    val analyticsTracker: AnalyticsTracker
    val crashReporter: CrashReporter
    val databaseManager: DatabaseManager
    val preferencesManager: PreferencesManager
    val secureStorage: SecureStorage
    val cacheManager: CacheManager
    val appLogger: AppLogger

    // ── ViewModel Accessors ──
    val homeViewModel: HomeViewModel
    val searchViewModel: SearchViewModel
    val productDetailViewModel: ProductDetailViewModel
    val cartViewModel: CartViewModel
    val checkoutViewModel: CheckoutViewModel
    val profileViewModel: ProfileViewModel
    val chatViewModel: ChatViewModel
    val orderHistoryViewModel: OrderHistoryViewModel
    val settingsViewModel: SettingsViewModel
    val notificationsViewModel: NotificationsViewModel
    val onboardingViewModel: OnboardingViewModel
    val reviewsViewModel: ReviewsViewModel
    val wishlistViewModel: WishlistViewModel

    val authInterceptor: AuthInterceptor
    val rateLimiter: RateLimiter
    val webSocketManager: WebSocketManager
    val graphQLClient: GraphQLClient
    val imageLoader: ImageLoader
    val featureFlagManager: FeatureFlagManager
    val appConfigProvider: AppConfigProvider
    val notificationManager: NotificationManager
    val deepLinkHandler: DeepLinkHandler
    val locationManager: LocationManager
    val storeLocator: StoreLocator
    val auditLogger: AuditLogger
    val eventBus: EventBus
    val remoteConfigManager: RemoteConfigManager
    val productRepository: ProductRepository
    val productRemoteDataSource: ProductRemoteDataSource
    val productLocalDataSource: ProductLocalDataSource
    val productMapper: ProductMapper
    val getProductListUseCase: GetProductListUseCase
    val getProductDetailUseCase: GetProductDetailUseCase
    val userRepository: UserRepository
    val userRemoteDataSource: UserRemoteDataSource
    val userLocalDataSource: UserLocalDataSource
    val userMapper: UserMapper
    val getUserListUseCase: GetUserListUseCase
    val getUserDetailUseCase: GetUserDetailUseCase
    val cartRepository: CartRepository
    val cartRemoteDataSource: CartRemoteDataSource
    val cartLocalDataSource: CartLocalDataSource
    val cartMapper: CartMapper
    val getCartListUseCase: GetCartListUseCase
    val getCartDetailUseCase: GetCartDetailUseCase
    val orderRepository: OrderRepository
    val orderRemoteDataSource: OrderRemoteDataSource
    val orderLocalDataSource: OrderLocalDataSource
    val orderMapper: OrderMapper
    val getOrderListUseCase: GetOrderListUseCase
    val getOrderDetailUseCase: GetOrderDetailUseCase
    val paymentRepository: PaymentRepository
    val paymentRemoteDataSource: PaymentRemoteDataSource
    val paymentLocalDataSource: PaymentLocalDataSource
    val paymentMapper: PaymentMapper
    val getPaymentListUseCase: GetPaymentListUseCase
    val getPaymentDetailUseCase: GetPaymentDetailUseCase
    val chatRepository: ChatRepository
    val chatRemoteDataSource: ChatRemoteDataSource
    val chatLocalDataSource: ChatLocalDataSource
    val chatMapper: ChatMapper
    val getChatListUseCase: GetChatListUseCase
    val getChatDetailUseCase: GetChatDetailUseCase
    val searchRepository: SearchRepository
    val searchRemoteDataSource: SearchRemoteDataSource
    val searchLocalDataSource: SearchLocalDataSource
    val searchMapper: SearchMapper
    val getSearchListUseCase: GetSearchListUseCase
    val getSearchDetailUseCase: GetSearchDetailUseCase
    val reviewRepository: ReviewRepository
    val reviewRemoteDataSource: ReviewRemoteDataSource
    val reviewLocalDataSource: ReviewLocalDataSource
    val reviewMapper: ReviewMapper
    val getReviewListUseCase: GetReviewListUseCase
    val getReviewDetailUseCase: GetReviewDetailUseCase
    val categoryRepository: CategoryRepository
    val categoryRemoteDataSource: CategoryRemoteDataSource
    val categoryLocalDataSource: CategoryLocalDataSource
    val categoryMapper: CategoryMapper
    val getCategoryListUseCase: GetCategoryListUseCase
    val getCategoryDetailUseCase: GetCategoryDetailUseCase
    val addressRepository: AddressRepository
    val addressRemoteDataSource: AddressRemoteDataSource
    val addressLocalDataSource: AddressLocalDataSource
    val addressMapper: AddressMapper
    val getAddressListUseCase: GetAddressListUseCase
    val getAddressDetailUseCase: GetAddressDetailUseCase
    val wishlistRepository: WishlistRepository
    val wishlistRemoteDataSource: WishlistRemoteDataSource
    val wishlistLocalDataSource: WishlistLocalDataSource
    val wishlistMapper: WishlistMapper
    val getWishlistListUseCase: GetWishlistListUseCase
    val getWishlistDetailUseCase: GetWishlistDetailUseCase
    val promotionRepository: PromotionRepository
    val promotionRemoteDataSource: PromotionRemoteDataSource
    val promotionLocalDataSource: PromotionLocalDataSource
    val promotionMapper: PromotionMapper
    val getPromotionListUseCase: GetPromotionListUseCase
    val getPromotionDetailUseCase: GetPromotionDetailUseCase
    val shippingRepository: ShippingRepository
    val shippingRemoteDataSource: ShippingRemoteDataSource
    val shippingLocalDataSource: ShippingLocalDataSource
    val shippingMapper: ShippingMapper
    val getShippingListUseCase: GetShippingListUseCase
    val getShippingDetailUseCase: GetShippingDetailUseCase
    val feedRepository: FeedRepository
    val feedRemoteDataSource: FeedRemoteDataSource
    val feedLocalDataSource: FeedLocalDataSource
    val feedMapper: FeedMapper
    val getFeedListUseCase: GetFeedListUseCase
    val getFeedDetailUseCase: GetFeedDetailUseCase
}
