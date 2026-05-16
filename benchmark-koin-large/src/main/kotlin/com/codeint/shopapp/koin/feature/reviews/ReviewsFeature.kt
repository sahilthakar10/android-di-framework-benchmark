package com.codeint.shopapp.koin.feature.reviews

import com.codeint.shopapp.koin.domain.review.*
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.auth.AuthManager


class ReviewListPresenter constructor(
    private val getReviewList: GetReviewListUseCase,
    private val searchReview: SearchReviewUseCase,
    private val analytics: AnalyticsTracker
) {
    fun loadReviews(productId: String): List<ReviewDomainModel> = getReviewList.execute().items
    fun searchReviews(query: String): List<ReviewDomainModel> = searchReview.execute(query).items
}

class WriteReviewPresenter constructor(
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
