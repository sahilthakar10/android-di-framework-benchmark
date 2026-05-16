package com.codeint.shopapp.koin.di

import org.koin.dsl.module
import com.codeint.shopapp.koin.feature.home.*
import com.codeint.shopapp.koin.feature.search.*
import com.codeint.shopapp.koin.feature.productdetail.*
import com.codeint.shopapp.koin.feature.cart.*
import com.codeint.shopapp.koin.feature.checkout.*
import com.codeint.shopapp.koin.feature.profile.*
import com.codeint.shopapp.koin.feature.orders.*
import com.codeint.shopapp.koin.feature.settings.*
import com.codeint.shopapp.koin.feature.chat.*
import com.codeint.shopapp.koin.feature.notifications.*
import com.codeint.shopapp.koin.feature.onboarding.*
import com.codeint.shopapp.koin.feature.reviews.*
import com.codeint.shopapp.koin.feature.wishlist.*

val featureHomeModule = module {
    factory { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    factory { BannerCarouselPresenter(get(), get()) }
    factory { TrendingProductsPresenter(get(), get()) }
    factory { RecentlyViewedManager(get(), get()) }
    factory { PersonalizedFeedPresenter(get(), get(), get()) }
}

val featureSearchModule = module {
    factory { SearchViewModel(get(), get(), get()) }
    factory { SearchSuggestionPresenter(get(), get()) }
    factory { FilterPresenter(get(), get()) }
    factory { SearchHistoryManager(get()) }
}

val featureProductDetailModule = module {
    factory { ProductDetailViewModel(get(), get(), get(), get(), get(), get()) }
    factory { RelatedProductsPresenter(get(), get()) }
    factory { ProductImageGalleryPresenter(get()) }
    factory { PriceCalculator(get()) }
    factory { StockChecker(get()) }
}

val featureCartModule = module {
    factory { CartViewModel(get(), get(), get(), get(), get()) }
    factory { CartCalculator(get()) }
    factory { CouponValidator(get(), get()) }
    factory { CartBadgeManager(get()) }
}

val featureCheckoutModule = module {
    factory { CheckoutViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { PaymentProcessor(get(), get()) }
    factory { ShippingCalculator(get()) }
    factory { OrderValidator(get()) }
}

val featureProfileModule = module {
    factory { ProfileViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { AddressManagerPresenter(get(), get(), get()) }
    factory { AccountSecurityPresenter(get(), get(), get()) }
}

val featureOrdersModule = module {
    factory { OrderHistoryViewModel(get(), get(), get()) }
    factory { OrderTrackingPresenter(get(), get()) }
    factory { ReturnRequestPresenter(get(), get()) }
}

val featureSettingsModule = module {
    factory { SettingsViewModel(get(), get(), get(), get(), get()) }
    factory { PrivacySettingsPresenter(get()) }
}

val featureChatModule = module {
    factory { ChatViewModel(get(), get(), get(), get(), get()) }
    factory { ChatNotificationHandler(get(), get()) }
    factory { TypingIndicatorManager(get()) }
}

val featureNotificationsModule = module {
    factory { NotificationsViewModel(get(), get(), get()) }
    factory { NotificationPreferencesPresenter(get(), get()) }
}

val featureOnboardingModule = module {
    factory { OnboardingViewModel(get(), get(), get(), get()) }
    factory { RegistrationPresenter(get(), get()) }
}

val featureReviewsModule = module {
    factory { ReviewListPresenter(get(), get(), get()) }
    factory { WriteReviewPresenter(get(), get(), get(), get()) }
}

val featureWishlistModule = module {
    factory { WishlistViewModel(get(), get(), get(), get()) }
    factory { WishlistSharePresenter(get()) }
}
