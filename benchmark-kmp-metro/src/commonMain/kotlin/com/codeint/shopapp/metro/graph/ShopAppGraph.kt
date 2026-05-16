package com.codeint.shopapp.metro.graph

import com.codeint.shopapp.metro.core.network.*
import com.codeint.shopapp.metro.core.auth.*
import com.codeint.shopapp.metro.core.analytics.*
import com.codeint.shopapp.metro.core.storage.*
import com.codeint.shopapp.metro.core.config.*
import com.codeint.shopapp.metro.core.logging.*
import com.codeint.shopapp.metro.core.image.*
import com.codeint.shopapp.metro.core.notification.*
import com.codeint.shopapp.metro.core.location.*
import com.codeint.shopapp.metro.data.product.*
import com.codeint.shopapp.metro.data.user.*
import com.codeint.shopapp.metro.data.cart.*
import com.codeint.shopapp.metro.data.order.*
import com.codeint.shopapp.metro.data.payment.*
import com.codeint.shopapp.metro.data.chat.*
import com.codeint.shopapp.metro.data.search.*
import com.codeint.shopapp.metro.data.review.*
import com.codeint.shopapp.metro.data.category.*
import com.codeint.shopapp.metro.data.address.*
import com.codeint.shopapp.metro.data.wishlist.*
import com.codeint.shopapp.metro.data.promotion.*
import com.codeint.shopapp.metro.data.shipping.*
import com.codeint.shopapp.metro.data.feed.*
import com.codeint.shopapp.metro.feature.home.*
import com.codeint.shopapp.metro.feature.search.*
import com.codeint.shopapp.metro.feature.productdetail.*
import com.codeint.shopapp.metro.feature.cart.*
import com.codeint.shopapp.metro.feature.checkout.*
import com.codeint.shopapp.metro.feature.profile.*
import com.codeint.shopapp.metro.feature.orders.*
import com.codeint.shopapp.metro.feature.settings.*
import com.codeint.shopapp.metro.feature.chat.*
import com.codeint.shopapp.metro.feature.notifications.*
import com.codeint.shopapp.metro.feature.onboarding.*
import com.codeint.shopapp.metro.feature.reviews.*
import com.codeint.shopapp.metro.feature.wishlist.*
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
interface ShopAppGraph {

    // Core Infrastructure
    @Provides fun provideHttpClient(): HttpClient = HttpClient("https://api.shopapp.com", 30_000)
    @Provides fun provideApiResponseParser(): ApiResponseParser = ApiResponseParser()
    @Provides fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor()
    @Provides fun provideRetryPolicy(): RetryPolicy = RetryPolicy(3, 1000)
    @Provides fun provideSslPinningConfig(): SslPinningConfig = SslPinningConfig(listOf("sha256/abc123"))

    // Core Services
    val authInterceptor: AuthInterceptor
    val cacheInterceptor: CacheInterceptor
    val loggingInterceptor: LoggingInterceptor
    val rateLimiter: RateLimiter
    val networkLogger: NetworkLogger
    val cachePolicy: CachePolicy
    val tokenProvider: TokenProvider
    val webSocketManager: WebSocketManager
    val graphQLClient: GraphQLClient
    val fileUploader: FileUploader
    val authManager: AuthManager
    val tokenStorage: TokenStorage
    val sessionManager: SessionManager
    val biometricAuthProvider: BiometricAuthProvider
    val oAuthManager: OAuthManager
    val passwordValidator: PasswordValidator
    val twoFactorAuthManager: TwoFactorAuthManager
    val analyticsTracker: AnalyticsTracker
    val crashReporter: CrashReporter
    val performanceMonitor: PerformanceMonitor
    val userPropertyTracker: UserPropertyTracker
    val abTestManager: ABTestManager
    val eventBus: EventBus
    val remoteConfigManager: RemoteConfigManager
    val consentManager: ConsentManager
    val databaseManager: DatabaseManager
    val preferencesManager: PreferencesManager
    val secureStorage: SecureStorage
    val cacheManager: CacheManager
    val fileManager: FileManager
    val downloadManager: DownloadManager
    val featureFlagManager: FeatureFlagManager
    val appConfigProvider: AppConfigProvider
    val themeManager: ThemeManager
    val localeManager: LocaleManager
    val environmentManager: EnvironmentManager
    val appLogger: AppLogger
    val auditLogger: AuditLogger
    val imageLoader: ImageLoader
    val imageProcessor: ImageProcessor
    val thumbnailGenerator: ThumbnailGenerator
    val notificationManager: NotificationManager
    val pushTokenManager: PushTokenManager
    val deepLinkHandler: DeepLinkHandler
    val inAppMessageManager: InAppMessageManager
    val locationManager: LocationManager
    val geocodingService: GeocodingService
    val storeLocator: StoreLocator

    // Repositories
    val productRepository: ProductRepository
    val userRepository: UserRepository
    val cartRepository: CartRepository
    val orderRepository: OrderRepository
    val paymentRepository: PaymentRepository
    val chatRepository: ChatRepository
    val searchRepository: SearchRepository
    val reviewRepository: ReviewRepository
    val categoryRepository: CategoryRepository
    val addressRepository: AddressRepository
    val wishlistRepository: WishlistRepository
    val promotionRepository: PromotionRepository
    val shippingRepository: ShippingRepository
    val feedRepository: FeedRepository

    // Features
    val homeViewModel: HomeViewModel
    val bannerCarouselPresenter: BannerCarouselPresenter
    val trendingProductsPresenter: TrendingProductsPresenter
    val recentlyViewedManager: RecentlyViewedManager
    val personalizedFeedPresenter: PersonalizedFeedPresenter
    val searchViewModel: SearchViewModel
    val searchSuggestionPresenter: SearchSuggestionPresenter
    val filterPresenter: FilterPresenter
    val searchHistoryManager: SearchHistoryManager
    val productDetailViewModel: ProductDetailViewModel
    val relatedProductsPresenter: RelatedProductsPresenter
    val productImageGalleryPresenter: ProductImageGalleryPresenter
    val priceCalculator: PriceCalculator
    val stockChecker: StockChecker
    val cartViewModel: CartViewModel
    val cartCalculator: CartCalculator
    val couponValidator: CouponValidator
    val cartBadgeManager: CartBadgeManager
    val checkoutViewModel: CheckoutViewModel
    val paymentProcessor: PaymentProcessor
    val shippingCalculator: ShippingCalculator
    val orderValidator: OrderValidator
    val profileViewModel: ProfileViewModel
    val addressManagerPresenter: AddressManagerPresenter
    val accountSecurityPresenter: AccountSecurityPresenter
    val orderHistoryViewModel: OrderHistoryViewModel
    val orderTrackingPresenter: OrderTrackingPresenter
    val returnRequestPresenter: ReturnRequestPresenter
    val settingsViewModel: SettingsViewModel
    val privacySettingsPresenter: PrivacySettingsPresenter
    val chatViewModel: ChatViewModel
    val chatNotificationHandler: ChatNotificationHandler
    val typingIndicatorManager: TypingIndicatorManager
    val notificationsViewModel: NotificationsViewModel
    val notificationPreferencesPresenter: NotificationPreferencesPresenter
    val onboardingViewModel: OnboardingViewModel
    val registrationPresenter: RegistrationPresenter
    val reviewListPresenter: ReviewListPresenter
    val writeReviewPresenter: WriteReviewPresenter
    val wishlistViewModel: WishlistViewModel
    val wishlistSharePresenter: WishlistSharePresenter
}
