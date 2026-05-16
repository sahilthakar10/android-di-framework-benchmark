package com.codeint.shopapp.hilt.feature.wishlist

import com.codeint.shopapp.hilt.domain.wishlist.*
import com.codeint.shopapp.hilt.domain.product.*
import com.codeint.shopapp.hilt.domain.cart.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import javax.inject.Inject

class WishlistViewModel @Inject constructor(
    private val getWishlistList: GetWishlistListUseCase,
    private val deleteWishlist: DeleteWishlistUseCase,
    private val createCart: CreateCartUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadWishlist(): List<WishlistDomainModel> { analytics.screen("wishlist"); return getWishlistList.execute().items }
    fun removeFromWishlist(id: String) { deleteWishlist.execute(id) }
    fun moveToCart(wishlistItem: WishlistDomainModel) { createCart.execute(CartDomainModel("", wishlistItem.name)); deleteWishlist.execute(wishlistItem.id) }
}

class WishlistSharePresenter @Inject constructor(private val getWishlistList: GetWishlistListUseCase) {
    fun generateShareLink(): String = "https://shopapp.com/wishlist/share/abc123"
}
