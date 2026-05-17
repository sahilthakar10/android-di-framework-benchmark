package com.codeint.shopapp.hilt.di

import com.codeint.shopapp.hilt.data.product.ProductRepository
import com.codeint.shopapp.hilt.data.product.OfflineFirstProductRepository
import com.codeint.shopapp.hilt.data.user.UserRepository
import com.codeint.shopapp.hilt.data.user.OfflineFirstUserRepository
import com.codeint.shopapp.hilt.data.cart.CartRepository
import com.codeint.shopapp.hilt.data.cart.OfflineFirstCartRepository
import com.codeint.shopapp.hilt.data.order.OrderRepository
import com.codeint.shopapp.hilt.data.order.OfflineFirstOrderRepository
import com.codeint.shopapp.hilt.data.payment.PaymentRepository
import com.codeint.shopapp.hilt.data.payment.OfflineFirstPaymentRepository
import com.codeint.shopapp.hilt.data.chat.ChatRepository
import com.codeint.shopapp.hilt.data.chat.OfflineFirstChatRepository
import com.codeint.shopapp.hilt.data.search.SearchRepository
import com.codeint.shopapp.hilt.data.search.OfflineFirstSearchRepository
import com.codeint.shopapp.hilt.data.review.ReviewRepository
import com.codeint.shopapp.hilt.data.review.OfflineFirstReviewRepository
import com.codeint.shopapp.hilt.data.category.CategoryRepository
import com.codeint.shopapp.hilt.data.category.OfflineFirstCategoryRepository
import com.codeint.shopapp.hilt.data.address.AddressRepository
import com.codeint.shopapp.hilt.data.address.OfflineFirstAddressRepository
import com.codeint.shopapp.hilt.data.wishlist.WishlistRepository
import com.codeint.shopapp.hilt.data.wishlist.OfflineFirstWishlistRepository
import com.codeint.shopapp.hilt.data.promotion.PromotionRepository
import com.codeint.shopapp.hilt.data.promotion.OfflineFirstPromotionRepository
import com.codeint.shopapp.hilt.data.shipping.ShippingRepository
import com.codeint.shopapp.hilt.data.shipping.OfflineFirstShippingRepository
import com.codeint.shopapp.hilt.data.feed.FeedRepository
import com.codeint.shopapp.hilt.data.feed.OfflineFirstFeedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    @Provides @Singleton
    fun providesProductRepository(impl: OfflineFirstProductRepository): ProductRepository = impl

    @Provides @Singleton
    fun providesUserRepository(impl: OfflineFirstUserRepository): UserRepository = impl

    @Provides @Singleton
    fun providesCartRepository(impl: OfflineFirstCartRepository): CartRepository = impl

    @Provides @Singleton
    fun providesOrderRepository(impl: OfflineFirstOrderRepository): OrderRepository = impl

    @Provides @Singleton
    fun providesPaymentRepository(impl: OfflineFirstPaymentRepository): PaymentRepository = impl

    @Provides @Singleton
    fun providesChatRepository(impl: OfflineFirstChatRepository): ChatRepository = impl

    @Provides @Singleton
    fun providesSearchRepository(impl: OfflineFirstSearchRepository): SearchRepository = impl

    @Provides @Singleton
    fun providesReviewRepository(impl: OfflineFirstReviewRepository): ReviewRepository = impl

    @Provides @Singleton
    fun providesCategoryRepository(impl: OfflineFirstCategoryRepository): CategoryRepository = impl

    @Provides @Singleton
    fun providesAddressRepository(impl: OfflineFirstAddressRepository): AddressRepository = impl

    @Provides @Singleton
    fun providesWishlistRepository(impl: OfflineFirstWishlistRepository): WishlistRepository = impl

    @Provides @Singleton
    fun providesPromotionRepository(impl: OfflineFirstPromotionRepository): PromotionRepository = impl

    @Provides @Singleton
    fun providesShippingRepository(impl: OfflineFirstShippingRepository): ShippingRepository = impl

    @Provides @Singleton
    fun providesFeedRepository(impl: OfflineFirstFeedRepository): FeedRepository = impl
}
