package com.codeint.shopapp.metro.feature.home

import com.codeint.shopapp.metro.domain.product.*
import com.codeint.shopapp.metro.domain.category.*
import com.codeint.shopapp.metro.domain.promotion.*
import com.codeint.shopapp.metro.domain.feed.*
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.config.FeatureFlagManager
import dev.zacsweers.metro.Inject

class HomeViewModel @Inject constructor(
    private val getProductList: GetProductListUseCase,
    private val getCategoryList: GetCategoryListUseCase,
    private val getPromotionList: GetPromotionListUseCase,
    private val getFeedList: GetFeedListUseCase,
    private val analytics: AnalyticsTracker,
    private val featureFlags: FeatureFlagManager
) {
    fun loadHome(): HomeScreenState {
        analytics.screen("home")
        val products = getProductList.execute(pageSize = 10)
        val categories = getCategoryList.execute()
        val promotions = getPromotionList.execute()
        return HomeScreenState(products.items, categories.items, promotions.items, featureFlags.isEnabled("new_home_banner"))
    }
}

class BannerCarouselPresenter @Inject constructor(
    private val getPromotionList: GetPromotionListUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadBanners(): List<PromotionDomainModel> {
        analytics.track("banner_load")
        return getPromotionList.execute().items
    }
    fun onBannerClick(id: String) { analytics.track("banner_click", mapOf("id" to id)) }
}

class TrendingProductsPresenter @Inject constructor(
    private val searchProduct: SearchProductUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadTrending(): List<ProductDomainModel> = searchProduct.execute("trending").items
}

class RecentlyViewedManager @Inject constructor(
    private val getProductDetail: GetProductDetailUseCase,
    private val prefs: com.codeint.shopapp.metro.core.storage.PreferencesManager
) {
    private val recentIds = mutableListOf<String>()
    fun addViewed(productId: String) { recentIds.add(0, productId); if (recentIds.size > 20) recentIds.removeLast() }
    fun getRecent(): List<ProductDomainModel> = recentIds.take(10).mapNotNull { getProductDetail.execute(it) }
}

class PersonalizedFeedPresenter @Inject constructor(
    private val getFeedList: GetFeedListUseCase,
    private val analytics: AnalyticsTracker,
    private val featureFlags: FeatureFlagManager
) {
    fun loadFeed(): List<FeedDomainModel> {
        if (!featureFlags.isEnabled("personalized_feed")) return emptyList()
        return getFeedList.execute().items
    }
}

data class HomeScreenState(
    val featuredProducts: List<ProductDomainModel>,
    val categories: List<CategoryDomainModel>,
    val promotions: List<PromotionDomainModel>,
    val showNewBanner: Boolean
)
