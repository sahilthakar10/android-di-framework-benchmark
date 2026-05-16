package com.codeint.shopapp.koin.feature.wishlist

import com.codeint.shopapp.koin.domain.wishlist.*
import com.codeint.shopapp.koin.domain.product.*
import com.codeint.shopapp.koin.domain.cart.*
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker


class WishlistViewModel constructor(
    private val getWishlistList: GetWishlistListUseCase,
    private val deleteWishlist: DeleteWishlistUseCase,
    private val createCart: CreateCartUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadWishlist(): List<WishlistDomainModel> { analytics.screen("wishlist"); return getWishlistList.execute().items }
    fun removeFromWishlist(id: String) { deleteWishlist.execute(id) }
    fun moveToCart(wishlistItem: WishlistDomainModel) { createCart.execute(CartDomainModel("", wishlistItem.name)); deleteWishlist.execute(wishlistItem.id) }
}

class WishlistSharePresenter constructor(private val getWishlistList: GetWishlistListUseCase) {
    fun generateShareLink(): String = "https://shopapp.com/wishlist/share/abc123"
}
