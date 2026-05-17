package com.codeint.shopapp.koin.di

import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import com.codeint.shopapp.koin.core.network.*
import com.codeint.shopapp.koin.core.auth.*
import com.codeint.shopapp.koin.core.analytics.*
import com.codeint.shopapp.koin.core.storage.*
import com.codeint.shopapp.koin.core.config.*
import com.codeint.shopapp.koin.core.logging.*
import com.codeint.shopapp.koin.core.image.*
import com.codeint.shopapp.koin.core.notification.*
import com.codeint.shopapp.koin.core.location.*
import com.codeint.shopapp.koin.data.product.*
import com.codeint.shopapp.koin.data.product.remote.*
import com.codeint.shopapp.koin.data.product.local.*
import com.codeint.shopapp.koin.data.product.mapper.*
import com.codeint.shopapp.koin.domain.product.*
import com.codeint.shopapp.koin.data.user.*
import com.codeint.shopapp.koin.data.user.remote.*
import com.codeint.shopapp.koin.data.user.local.*
import com.codeint.shopapp.koin.data.user.mapper.*
import com.codeint.shopapp.koin.domain.user.*
import com.codeint.shopapp.koin.data.cart.*
import com.codeint.shopapp.koin.data.cart.remote.*
import com.codeint.shopapp.koin.data.cart.local.*
import com.codeint.shopapp.koin.data.cart.mapper.*
import com.codeint.shopapp.koin.domain.cart.*
import com.codeint.shopapp.koin.data.order.*
import com.codeint.shopapp.koin.data.order.remote.*
import com.codeint.shopapp.koin.data.order.local.*
import com.codeint.shopapp.koin.data.order.mapper.*
import com.codeint.shopapp.koin.domain.order.*
import com.codeint.shopapp.koin.data.payment.*
import com.codeint.shopapp.koin.data.payment.remote.*
import com.codeint.shopapp.koin.data.payment.local.*
import com.codeint.shopapp.koin.data.payment.mapper.*
import com.codeint.shopapp.koin.domain.payment.*
import com.codeint.shopapp.koin.data.chat.*
import com.codeint.shopapp.koin.data.chat.remote.*
import com.codeint.shopapp.koin.data.chat.local.*
import com.codeint.shopapp.koin.data.chat.mapper.*
import com.codeint.shopapp.koin.domain.chat.*
import com.codeint.shopapp.koin.data.search.*
import com.codeint.shopapp.koin.data.search.remote.*
import com.codeint.shopapp.koin.data.search.local.*
import com.codeint.shopapp.koin.data.search.mapper.*
import com.codeint.shopapp.koin.domain.search.*
import com.codeint.shopapp.koin.data.review.*
import com.codeint.shopapp.koin.data.review.remote.*
import com.codeint.shopapp.koin.data.review.local.*
import com.codeint.shopapp.koin.data.review.mapper.*
import com.codeint.shopapp.koin.domain.review.*
import com.codeint.shopapp.koin.data.category.*
import com.codeint.shopapp.koin.data.category.remote.*
import com.codeint.shopapp.koin.data.category.local.*
import com.codeint.shopapp.koin.data.category.mapper.*
import com.codeint.shopapp.koin.domain.category.*
import com.codeint.shopapp.koin.data.address.*
import com.codeint.shopapp.koin.data.address.remote.*
import com.codeint.shopapp.koin.data.address.local.*
import com.codeint.shopapp.koin.data.address.mapper.*
import com.codeint.shopapp.koin.domain.address.*
import com.codeint.shopapp.koin.data.wishlist.*
import com.codeint.shopapp.koin.data.wishlist.remote.*
import com.codeint.shopapp.koin.data.wishlist.local.*
import com.codeint.shopapp.koin.data.wishlist.mapper.*
import com.codeint.shopapp.koin.domain.wishlist.*
import com.codeint.shopapp.koin.data.promotion.*
import com.codeint.shopapp.koin.data.promotion.remote.*
import com.codeint.shopapp.koin.data.promotion.local.*
import com.codeint.shopapp.koin.data.promotion.mapper.*
import com.codeint.shopapp.koin.domain.promotion.*
import com.codeint.shopapp.koin.data.shipping.*
import com.codeint.shopapp.koin.data.shipping.remote.*
import com.codeint.shopapp.koin.data.shipping.local.*
import com.codeint.shopapp.koin.data.shipping.mapper.*
import com.codeint.shopapp.koin.domain.shipping.*
import com.codeint.shopapp.koin.data.feed.*
import com.codeint.shopapp.koin.data.feed.remote.*
import com.codeint.shopapp.koin.data.feed.local.*
import com.codeint.shopapp.koin.data.feed.mapper.*
import com.codeint.shopapp.koin.domain.feed.*
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

val coreNetworkModule = module {
    single(createdAtStart = true) { HttpClient("https://api.shopapp.com", 30_000) }
    single(createdAtStart = true) { ApiResponseParser() }
    single { NetworkMonitor() }
    single { RetryPolicy(3, 1000) }
    single { SslPinningConfig(listOf("sha256/abc123")) }
    single<TokenProvider>(createdAtStart = true) { RealTokenProvider() }
    single<NetworkLogger> { RealNetworkLogger() }
    single<CachePolicy> { RealCachePolicy() }
    single { AuthInterceptor(get()) }
    single { CacheInterceptor(get()) }
    single { LoggingInterceptor(get()) }
    single { RateLimiter(get()) }
    single { WebSocketManager(get(), get()) }
    single { GraphQLClient(get(), get(), get()) }
    single { FileUploader(get(), get()) }
}

val coreStorageModule = module {
    single<DatabaseManager>(createdAtStart = true) { RealDatabaseManager() }
    single<PreferencesManager>(createdAtStart = true) { RealPreferencesManager() }
    single<SecureStorage>(createdAtStart = true) { RealSecureStorage() }
    single<CacheManager>(createdAtStart = true) { RealCacheManager(get()) }
    single { FileManager() }
    single { DownloadManager(get()) }
}

val coreAuthModule = module {
    single<TokenStorage>(createdAtStart = true) { RealTokenStorage(get()) }
    single<SessionManager>(createdAtStart = true) { RealSessionManager(get()) }
    single<AuthManager>(createdAtStart = true) { RealAuthManager(get(), get(), get()) }
    single { BiometricAuthProvider(get()) }
    single { OAuthManager(get(), get()) }
    single { PasswordValidator() }
    single { TwoFactorAuthManager(get(), get()) }
}

val coreAnalyticsModule = module {
    single(createdAtStart = true) { EventBus() }
    single(createdAtStart = true) { UserPropertyTracker() }
    single(createdAtStart = true) { RemoteConfigManager() }
    single<AnalyticsTracker>(createdAtStart = true) { RealAnalyticsTracker(get(), get()) }
    single<CrashReporter>(createdAtStart = true) { RealCrashReporter() }
    single { PerformanceMonitor() }
    single { ABTestManager(get()) }
    single { ConsentManager(get()) }
}

val coreConfigModule = module {
    single(createdAtStart = true) { FeatureFlagManager(get()) }
    single { AppConfigProvider(get()) }
    single { ThemeManager(get()) }
    single { LocaleManager(get()) }
    single { EnvironmentManager() }
}

val coreLoggingModule = module {
    single<AppLogger>(createdAtStart = true) { RealAppLogger(get()) }
    single { AuditLogger(get()) }
}

val coreImageModule = module {
    single { ImageLoader(get(), get()) }
    single { ImageProcessor() }
    single { ThumbnailGenerator(get()) }
}

val coreNotificationModule = module {
    single { NotificationManager(get()) }
    single { PushTokenManager(get()) }
    single { DeepLinkHandler() }
    single { InAppMessageManager(get(), get()) }
}

val coreLocationModule = module {
    single { LocationManager(get()) }
    single { GeocodingService() }
    single { StoreLocator(get(), get()) }
}

val dataModule = module {
    single { ProductRemoteDataSource(get(), get(), get(), get()) }
    single { ProductLocalDataSource(get(), get()) }
    single { ProductMapper() }
    single<ProductRepository> { OfflineFirstProductRepository(get(), get(), get(), get()) }
    single { UserRemoteDataSource(get(), get(), get(), get()) }
    single { UserLocalDataSource(get(), get()) }
    single { UserMapper() }
    single<UserRepository> { OfflineFirstUserRepository(get(), get(), get(), get()) }
    single { CartRemoteDataSource(get(), get(), get(), get()) }
    single { CartLocalDataSource(get(), get()) }
    single { CartMapper() }
    single<CartRepository> { OfflineFirstCartRepository(get(), get(), get(), get()) }
    single { OrderRemoteDataSource(get(), get(), get(), get()) }
    single { OrderLocalDataSource(get(), get()) }
    single { OrderMapper() }
    single<OrderRepository> { OfflineFirstOrderRepository(get(), get(), get(), get()) }
    single { PaymentRemoteDataSource(get(), get(), get(), get()) }
    single { PaymentLocalDataSource(get(), get()) }
    single { PaymentMapper() }
    single<PaymentRepository> { OfflineFirstPaymentRepository(get(), get(), get(), get()) }
    single { ChatRemoteDataSource(get(), get(), get(), get()) }
    single { ChatLocalDataSource(get(), get()) }
    single { ChatMapper() }
    single<ChatRepository> { OfflineFirstChatRepository(get(), get(), get(), get()) }
    single { SearchRemoteDataSource(get(), get(), get(), get()) }
    single { SearchLocalDataSource(get(), get()) }
    single { SearchMapper() }
    single<SearchRepository> { OfflineFirstSearchRepository(get(), get(), get(), get()) }
    single { ReviewRemoteDataSource(get(), get(), get(), get()) }
    single { ReviewLocalDataSource(get(), get()) }
    single { ReviewMapper() }
    single<ReviewRepository> { OfflineFirstReviewRepository(get(), get(), get(), get()) }
    single { CategoryRemoteDataSource(get(), get(), get(), get()) }
    single { CategoryLocalDataSource(get(), get()) }
    single { CategoryMapper() }
    single<CategoryRepository> { OfflineFirstCategoryRepository(get(), get(), get(), get()) }
    single { AddressRemoteDataSource(get(), get(), get(), get()) }
    single { AddressLocalDataSource(get(), get()) }
    single { AddressMapper() }
    single<AddressRepository> { OfflineFirstAddressRepository(get(), get(), get(), get()) }
    single { WishlistRemoteDataSource(get(), get(), get(), get()) }
    single { WishlistLocalDataSource(get(), get()) }
    single { WishlistMapper() }
    single<WishlistRepository> { OfflineFirstWishlistRepository(get(), get(), get(), get()) }
    single { PromotionRemoteDataSource(get(), get(), get(), get()) }
    single { PromotionLocalDataSource(get(), get()) }
    single { PromotionMapper() }
    single<PromotionRepository> { OfflineFirstPromotionRepository(get(), get(), get(), get()) }
    single { ShippingRemoteDataSource(get(), get(), get(), get()) }
    single { ShippingLocalDataSource(get(), get()) }
    single { ShippingMapper() }
    single<ShippingRepository> { OfflineFirstShippingRepository(get(), get(), get(), get()) }
    single { FeedRemoteDataSource(get(), get(), get(), get()) }
    single { FeedLocalDataSource(get(), get()) }
    single { FeedMapper() }
    single<FeedRepository> { OfflineFirstFeedRepository(get(), get(), get(), get()) }
}

val domainModule = module {
    factory { GetProductListUseCase(get(), get()) }
    factory { GetProductDetailUseCase(get(), get()) }
    factory { CreateProductUseCase(get(), get()) }
    factory { UpdateProductUseCase(get(), get()) }
    factory { DeleteProductUseCase(get(), get()) }
    factory { SearchProductUseCase(get(), get()) }
    factory { ValidateProductUseCase(get()) }
    factory { RefreshProductCacheUseCase(get(), get()) }
    factory { GetProductCountUseCase(get()) }
    factory { FilterProductUseCase(get()) }
    factory { GetUserListUseCase(get(), get()) }
    factory { GetUserDetailUseCase(get(), get()) }
    factory { CreateUserUseCase(get(), get()) }
    factory { UpdateUserUseCase(get(), get()) }
    factory { DeleteUserUseCase(get(), get()) }
    factory { SearchUserUseCase(get(), get()) }
    factory { ValidateUserUseCase(get()) }
    factory { RefreshUserCacheUseCase(get(), get()) }
    factory { GetUserCountUseCase(get()) }
    factory { FilterUserUseCase(get()) }
    factory { GetCartListUseCase(get(), get()) }
    factory { GetCartDetailUseCase(get(), get()) }
    factory { CreateCartUseCase(get(), get()) }
    factory { UpdateCartUseCase(get(), get()) }
    factory { DeleteCartUseCase(get(), get()) }
    factory { SearchCartUseCase(get(), get()) }
    factory { ValidateCartUseCase(get()) }
    factory { RefreshCartCacheUseCase(get(), get()) }
    factory { GetCartCountUseCase(get()) }
    factory { FilterCartUseCase(get()) }
    factory { GetOrderListUseCase(get(), get()) }
    factory { GetOrderDetailUseCase(get(), get()) }
    factory { CreateOrderUseCase(get(), get()) }
    factory { UpdateOrderUseCase(get(), get()) }
    factory { DeleteOrderUseCase(get(), get()) }
    factory { SearchOrderUseCase(get(), get()) }
    factory { ValidateOrderUseCase(get()) }
    factory { RefreshOrderCacheUseCase(get(), get()) }
    factory { GetOrderCountUseCase(get()) }
    factory { FilterOrderUseCase(get()) }
    factory { GetPaymentListUseCase(get(), get()) }
    factory { GetPaymentDetailUseCase(get(), get()) }
    factory { CreatePaymentUseCase(get(), get()) }
    factory { UpdatePaymentUseCase(get(), get()) }
    factory { DeletePaymentUseCase(get(), get()) }
    factory { SearchPaymentUseCase(get(), get()) }
    factory { ValidatePaymentUseCase(get()) }
    factory { RefreshPaymentCacheUseCase(get(), get()) }
    factory { GetPaymentCountUseCase(get()) }
    factory { FilterPaymentUseCase(get()) }
    factory { GetChatListUseCase(get(), get()) }
    factory { GetChatDetailUseCase(get(), get()) }
    factory { CreateChatUseCase(get(), get()) }
    factory { UpdateChatUseCase(get(), get()) }
    factory { DeleteChatUseCase(get(), get()) }
    factory { SearchChatUseCase(get(), get()) }
    factory { ValidateChatUseCase(get()) }
    factory { RefreshChatCacheUseCase(get(), get()) }
    factory { GetChatCountUseCase(get()) }
    factory { FilterChatUseCase(get()) }
    factory { GetSearchListUseCase(get(), get()) }
    factory { GetSearchDetailUseCase(get(), get()) }
    factory { CreateSearchUseCase(get(), get()) }
    factory { UpdateSearchUseCase(get(), get()) }
    factory { DeleteSearchUseCase(get(), get()) }
    factory { SearchSearchUseCase(get(), get()) }
    factory { ValidateSearchUseCase(get()) }
    factory { RefreshSearchCacheUseCase(get(), get()) }
    factory { GetSearchCountUseCase(get()) }
    factory { FilterSearchUseCase(get()) }
    factory { GetReviewListUseCase(get(), get()) }
    factory { GetReviewDetailUseCase(get(), get()) }
    factory { CreateReviewUseCase(get(), get()) }
    factory { UpdateReviewUseCase(get(), get()) }
    factory { DeleteReviewUseCase(get(), get()) }
    factory { SearchReviewUseCase(get(), get()) }
    factory { ValidateReviewUseCase(get()) }
    factory { RefreshReviewCacheUseCase(get(), get()) }
    factory { GetReviewCountUseCase(get()) }
    factory { FilterReviewUseCase(get()) }
    factory { GetCategoryListUseCase(get(), get()) }
    factory { GetCategoryDetailUseCase(get(), get()) }
    factory { CreateCategoryUseCase(get(), get()) }
    factory { UpdateCategoryUseCase(get(), get()) }
    factory { DeleteCategoryUseCase(get(), get()) }
    factory { SearchCategoryUseCase(get(), get()) }
    factory { ValidateCategoryUseCase(get()) }
    factory { RefreshCategoryCacheUseCase(get(), get()) }
    factory { GetCategoryCountUseCase(get()) }
    factory { FilterCategoryUseCase(get()) }
    factory { GetAddressListUseCase(get(), get()) }
    factory { GetAddressDetailUseCase(get(), get()) }
    factory { CreateAddressUseCase(get(), get()) }
    factory { UpdateAddressUseCase(get(), get()) }
    factory { DeleteAddressUseCase(get(), get()) }
    factory { SearchAddressUseCase(get(), get()) }
    factory { ValidateAddressUseCase(get()) }
    factory { RefreshAddressCacheUseCase(get(), get()) }
    factory { GetAddressCountUseCase(get()) }
    factory { FilterAddressUseCase(get()) }
    factory { GetWishlistListUseCase(get(), get()) }
    factory { GetWishlistDetailUseCase(get(), get()) }
    factory { CreateWishlistUseCase(get(), get()) }
    factory { UpdateWishlistUseCase(get(), get()) }
    factory { DeleteWishlistUseCase(get(), get()) }
    factory { SearchWishlistUseCase(get(), get()) }
    factory { ValidateWishlistUseCase(get()) }
    factory { RefreshWishlistCacheUseCase(get(), get()) }
    factory { GetWishlistCountUseCase(get()) }
    factory { FilterWishlistUseCase(get()) }
    factory { GetPromotionListUseCase(get(), get()) }
    factory { GetPromotionDetailUseCase(get(), get()) }
    factory { CreatePromotionUseCase(get(), get()) }
    factory { UpdatePromotionUseCase(get(), get()) }
    factory { DeletePromotionUseCase(get(), get()) }
    factory { SearchPromotionUseCase(get(), get()) }
    factory { ValidatePromotionUseCase(get()) }
    factory { RefreshPromotionCacheUseCase(get(), get()) }
    factory { GetPromotionCountUseCase(get()) }
    factory { FilterPromotionUseCase(get()) }
    factory { GetShippingListUseCase(get(), get()) }
    factory { GetShippingDetailUseCase(get(), get()) }
    factory { CreateShippingUseCase(get(), get()) }
    factory { UpdateShippingUseCase(get(), get()) }
    factory { DeleteShippingUseCase(get(), get()) }
    factory { SearchShippingUseCase(get(), get()) }
    factory { ValidateShippingUseCase(get()) }
    factory { RefreshShippingCacheUseCase(get(), get()) }
    factory { GetShippingCountUseCase(get()) }
    factory { FilterShippingUseCase(get()) }
    factory { GetFeedListUseCase(get(), get()) }
    factory { GetFeedDetailUseCase(get(), get()) }
    factory { CreateFeedUseCase(get(), get()) }
    factory { UpdateFeedUseCase(get(), get()) }
    factory { DeleteFeedUseCase(get(), get()) }
    factory { SearchFeedUseCase(get(), get()) }
    factory { ValidateFeedUseCase(get()) }
    factory { RefreshFeedCacheUseCase(get(), get()) }
    factory { GetFeedCountUseCase(get()) }
    factory { FilterFeedUseCase(get()) }
}

val featureModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { ProductDetailViewModel(get(), get(), get()) }
    viewModel { CartViewModel(get(), get(), get()) }
    viewModel { CheckoutViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { OrderHistoryViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { NotificationsViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { ReviewsViewModel(get(), get()) }
    viewModel { WishlistViewModel(get(), get()) }
}

val allShopAppModules = listOf(
    coreNetworkModule, coreStorageModule, coreAuthModule, coreAnalyticsModule,
    coreConfigModule, coreLoggingModule, coreImageModule, coreNotificationModule,
    coreLocationModule, dataModule, domainModule, featureModule
)
