package com.codeint.shopapp.koin.di

import org.koin.dsl.module
import com.codeint.shopapp.koin.domain.product.*
import com.codeint.shopapp.koin.domain.user.*
import com.codeint.shopapp.koin.domain.cart.*
import com.codeint.shopapp.koin.domain.order.*
import com.codeint.shopapp.koin.domain.payment.*
import com.codeint.shopapp.koin.domain.chat.*
import com.codeint.shopapp.koin.domain.search.*
import com.codeint.shopapp.koin.domain.review.*
import com.codeint.shopapp.koin.domain.category.*
import com.codeint.shopapp.koin.domain.address.*
import com.codeint.shopapp.koin.domain.wishlist.*
import com.codeint.shopapp.koin.domain.promotion.*
import com.codeint.shopapp.koin.domain.shipping.*
import com.codeint.shopapp.koin.domain.feed.*

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
