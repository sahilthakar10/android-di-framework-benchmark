package com.codeint.shopapp.metro.feature.reviews

import com.codeint.shopapp.metro.domain.review.*
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.auth.AuthManager
import dev.zacsweers.metro.Inject

class ReviewListPresenter @Inject constructor(
    private val getReviewList: GetReviewListUseCase,
    private val searchReview: SearchReviewUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadReviews(productId: String): List<ReviewDomainModel> = getReviewList.execute().items
    fun searchReviews(query: String): List<ReviewDomainModel> = searchReview.execute(query).items
}

class WriteReviewPresenter @Inject constructor(
    private val createReview: CreateReviewUseCase,
    private val validateReview: ValidateReviewUseCase,
    private val authManager: AuthManager,
    private val analytics: AnalyticsTracker
) {
    fun submitReview(productId: String, rating: Int, text: String): Boolean {
        if (!authManager.isLoggedIn()) return false
        analytics.track("submit_review", mapOf("product_id" to productId, "rating" to rating))
        createReview.execute(ReviewDomainModel("", "Review: $text"))
        return true
    }
}
