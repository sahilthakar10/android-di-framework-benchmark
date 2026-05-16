package com.codeint.shopapp.hilt.feature.search

import com.codeint.shopapp.hilt.domain.product.*
import com.codeint.shopapp.hilt.domain.search.*
import com.codeint.shopapp.hilt.domain.category.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.storage.PreferencesManager
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchProduct: SearchProductUseCase,
    private val searchSearch: SearchSearchUseCase,
    private val analytics: AnalyticsTracker
) {
    fun search(query: String, page: Int = 0): SearchScreenState {
        analytics.track("search", mapOf("query" to query))
        val results = searchProduct.execute(query, page)
        return SearchScreenState(results.items, results.totalCount, results.hasMore, query)
    }
}

class SearchSuggestionPresenter @Inject constructor(
    private val prefs: PreferencesManager,
    private val searchSearch: SearchSearchUseCase
) {
    fun getRecentSearches(): List<String> = prefs.getString("recent_searches")?.split(",") ?: emptyList()
    fun saveSearch(query: String) { val recent = getRecentSearches().toMutableList(); recent.add(0, query); prefs.putString("recent_searches", recent.take(10).joinToString(",")) }
    fun getSuggestions(prefix: String): List<String> = searchSearch.execute(prefix).items.map { it.name }
}

class FilterPresenter @Inject constructor(
    private val getCategoryList: GetCategoryListUseCase,
    private val filterProduct: FilterProductUseCase
) {
    fun getAvailableFilters(): FilterOptions = FilterOptions(getCategoryList.execute().items.map { it.name }, listOf("Low to High", "High to Low", "Newest", "Popular"), listOf("0-25", "25-50", "50-100", "100+"))
    fun applyFilters(filters: Map<String, String>): List<ProductDomainModel> = filterProduct.execute { true }
}

class SearchHistoryManager @Inject constructor(private val prefs: PreferencesManager) {
    fun addToHistory(query: String) {}
    fun getHistory(): List<String> = emptyList()
    fun clearHistory() {}
}

data class SearchScreenState(val results: List<ProductDomainModel>, val totalCount: Int, val hasMore: Boolean, val query: String)
data class FilterOptions(val categories: List<String>, val sortOptions: List<String>, val priceRanges: List<String>)
