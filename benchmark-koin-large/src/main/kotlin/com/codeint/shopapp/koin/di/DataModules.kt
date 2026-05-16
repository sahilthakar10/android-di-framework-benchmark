package com.codeint.shopapp.koin.di

import org.koin.dsl.module
import com.codeint.shopapp.koin.data.product.*
import com.codeint.shopapp.koin.data.product.remote.*
import com.codeint.shopapp.koin.data.product.local.*
import com.codeint.shopapp.koin.data.product.mapper.*
import com.codeint.shopapp.koin.data.user.*
import com.codeint.shopapp.koin.data.user.remote.*
import com.codeint.shopapp.koin.data.user.local.*
import com.codeint.shopapp.koin.data.user.mapper.*
import com.codeint.shopapp.koin.data.cart.*
import com.codeint.shopapp.koin.data.cart.remote.*
import com.codeint.shopapp.koin.data.cart.local.*
import com.codeint.shopapp.koin.data.cart.mapper.*
import com.codeint.shopapp.koin.data.order.*
import com.codeint.shopapp.koin.data.order.remote.*
import com.codeint.shopapp.koin.data.order.local.*
import com.codeint.shopapp.koin.data.order.mapper.*
import com.codeint.shopapp.koin.data.payment.*
import com.codeint.shopapp.koin.data.payment.remote.*
import com.codeint.shopapp.koin.data.payment.local.*
import com.codeint.shopapp.koin.data.payment.mapper.*
import com.codeint.shopapp.koin.data.chat.*
import com.codeint.shopapp.koin.data.chat.remote.*
import com.codeint.shopapp.koin.data.chat.local.*
import com.codeint.shopapp.koin.data.chat.mapper.*
import com.codeint.shopapp.koin.data.search.*
import com.codeint.shopapp.koin.data.search.remote.*
import com.codeint.shopapp.koin.data.search.local.*
import com.codeint.shopapp.koin.data.search.mapper.*
import com.codeint.shopapp.koin.data.review.*
import com.codeint.shopapp.koin.data.review.remote.*
import com.codeint.shopapp.koin.data.review.local.*
import com.codeint.shopapp.koin.data.review.mapper.*
import com.codeint.shopapp.koin.data.category.*
import com.codeint.shopapp.koin.data.category.remote.*
import com.codeint.shopapp.koin.data.category.local.*
import com.codeint.shopapp.koin.data.category.mapper.*
import com.codeint.shopapp.koin.data.address.*
import com.codeint.shopapp.koin.data.address.remote.*
import com.codeint.shopapp.koin.data.address.local.*
import com.codeint.shopapp.koin.data.address.mapper.*
import com.codeint.shopapp.koin.data.wishlist.*
import com.codeint.shopapp.koin.data.wishlist.remote.*
import com.codeint.shopapp.koin.data.wishlist.local.*
import com.codeint.shopapp.koin.data.wishlist.mapper.*
import com.codeint.shopapp.koin.data.promotion.*
import com.codeint.shopapp.koin.data.promotion.remote.*
import com.codeint.shopapp.koin.data.promotion.local.*
import com.codeint.shopapp.koin.data.promotion.mapper.*
import com.codeint.shopapp.koin.data.shipping.*
import com.codeint.shopapp.koin.data.shipping.remote.*
import com.codeint.shopapp.koin.data.shipping.local.*
import com.codeint.shopapp.koin.data.shipping.mapper.*
import com.codeint.shopapp.koin.data.feed.*
import com.codeint.shopapp.koin.data.feed.remote.*
import com.codeint.shopapp.koin.data.feed.local.*
import com.codeint.shopapp.koin.data.feed.mapper.*
import com.codeint.shopapp.koin.core.logging.AppLogger

val dataModule = module {

    single { ProductRemoteDataSource(get(), get(), get(), get()) }
    single { ProductLocalDataSource(get(), get()) }
    single { ProductMapper() }
    single { ProductRepository(get(), get(), get(), get()) }

    single { UserRemoteDataSource(get(), get(), get(), get()) }
    single { UserLocalDataSource(get(), get()) }
    single { UserMapper() }
    single { UserRepository(get(), get(), get(), get()) }

    single { CartRemoteDataSource(get(), get(), get(), get()) }
    single { CartLocalDataSource(get(), get()) }
    single { CartMapper() }
    single { CartRepository(get(), get(), get(), get()) }

    single { OrderRemoteDataSource(get(), get(), get(), get()) }
    single { OrderLocalDataSource(get(), get()) }
    single { OrderMapper() }
    single { OrderRepository(get(), get(), get(), get()) }

    single { PaymentRemoteDataSource(get(), get(), get(), get()) }
    single { PaymentLocalDataSource(get(), get()) }
    single { PaymentMapper() }
    single { PaymentRepository(get(), get(), get(), get()) }

    single { ChatRemoteDataSource(get(), get(), get(), get()) }
    single { ChatLocalDataSource(get(), get()) }
    single { ChatMapper() }
    single { ChatRepository(get(), get(), get(), get()) }

    single { SearchRemoteDataSource(get(), get(), get(), get()) }
    single { SearchLocalDataSource(get(), get()) }
    single { SearchMapper() }
    single { SearchRepository(get(), get(), get(), get()) }

    single { ReviewRemoteDataSource(get(), get(), get(), get()) }
    single { ReviewLocalDataSource(get(), get()) }
    single { ReviewMapper() }
    single { ReviewRepository(get(), get(), get(), get()) }

    single { CategoryRemoteDataSource(get(), get(), get(), get()) }
    single { CategoryLocalDataSource(get(), get()) }
    single { CategoryMapper() }
    single { CategoryRepository(get(), get(), get(), get()) }

    single { AddressRemoteDataSource(get(), get(), get(), get()) }
    single { AddressLocalDataSource(get(), get()) }
    single { AddressMapper() }
    single { AddressRepository(get(), get(), get(), get()) }

    single { WishlistRemoteDataSource(get(), get(), get(), get()) }
    single { WishlistLocalDataSource(get(), get()) }
    single { WishlistMapper() }
    single { WishlistRepository(get(), get(), get(), get()) }

    single { PromotionRemoteDataSource(get(), get(), get(), get()) }
    single { PromotionLocalDataSource(get(), get()) }
    single { PromotionMapper() }
    single { PromotionRepository(get(), get(), get(), get()) }

    single { ShippingRemoteDataSource(get(), get(), get(), get()) }
    single { ShippingLocalDataSource(get(), get()) }
    single { ShippingMapper() }
    single { ShippingRepository(get(), get(), get(), get()) }

    single { FeedRemoteDataSource(get(), get(), get(), get()) }
    single { FeedLocalDataSource(get(), get()) }
    single { FeedMapper() }
    single { FeedRepository(get(), get(), get(), get()) }

}
