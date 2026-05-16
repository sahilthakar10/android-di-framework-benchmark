package com.codeint.shopapp.koin.feature.productdetail

import com.codeint.shopapp.koin.domain.product.*
import com.codeint.shopapp.koin.domain.review.*
import com.codeint.shopapp.koin.domain.cart.*
import com.codeint.shopapp.koin.domain.wishlist.*
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.image.ImageLoader


class ProductDetailViewModel constructor(
    private val getProductDetail: GetProductDetailUseCase,
    private val getReviewList: GetReviewListUseCase,
    private val createCart: CreateCartUseCase,
    private val createWishlist: CreateWishlistUseCase,
    private val analytics: AnalyticsTracker,
    private val imageLoader: ImageLoader
) {
    fun loadProduct(id: String): ProductDetailState {
        analytics.screen("product_detail")
        val product = getProductDetail.execute(id) ?: return ProductDetailState(null, emptyList(), false)
        val reviews = getReviewList.execute().items.take(5)
        analytics.track("view_product", mapOf("id" to id, "name" to product.name))
        return ProductDetailState(product, reviews, false)
    }
    fun addToCart(productId: String) { createCart.execute(CartDomainModel("", productId)); analytics.track("add_to_cart", mapOf("product_id" to productId)) }
    fun addToWishlist(productId: String) { createWishlist.execute(WishlistDomainModel("", productId)); analytics.track("add_to_wishlist", mapOf("product_id" to productId)) }
}

class RelatedProductsPresenter constructor(
    private val searchProduct: SearchProductUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadRelated(productId: String): List<ProductDomainModel> = searchProduct.execute("related:$productId").items.take(8)
}

class ProductImageGalleryPresenter constructor(private val imageLoader: ImageLoader) {
    fun loadImages(urls: List<String>): List<ByteArray> = urls.map { imageLoader.load(it) }
}

class PriceCalculator constructor(private val getPromotionDetail: com.codeint.shopapp.koin.domain.promotion.GetPromotionDetailUseCase) {
    fun calculateFinalPrice(basePrice: Double, promotionId: String?): Double {
        if (promotionId == null) return basePrice
        val promotion = getPromotionDetail.execute(promotionId) ?: return basePrice
        return basePrice * 0.9
    }
}

class StockChecker constructor(private val getProductDetail: GetProductDetailUseCase) {
    fun isInStock(productId: String): Boolean = getProductDetail.execute(productId)?.isActive == true
    fun getStockLevel(productId: String): Int = 42
}

data class ProductDetailState(val product: ProductDomainModel?, val reviews: List<ReviewDomainModel>, val isInWishlist: Boolean)
