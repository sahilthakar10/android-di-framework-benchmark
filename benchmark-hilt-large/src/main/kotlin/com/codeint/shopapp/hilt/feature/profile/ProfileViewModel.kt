package com.codeint.shopapp.hilt.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeint.shopapp.hilt.domain.user.*
import com.codeint.shopapp.hilt.domain.order.*
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserDetail: GetUserDetailUseCase,
    private val getOrderList: GetOrderListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileViewModelUiState>(ProfileViewModelUiState.Loading)
    val uiState: StateFlow<ProfileViewModelUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ProfileViewModelUiState.Loading
            try {
                analytics.screen("profile")
                val user = getUserDetail.execute("current")
                val orders = getOrderList.execute()
                _uiState.value = ProfileViewModelUiState.Success(user?.name ?: "Guest", orders.totalCount)
            } catch (e: Exception) {
                _uiState.value = ProfileViewModelUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface ProfileViewModelUiState {
    data object Loading : ProfileViewModelUiState
    data class Success(val userName: String, val orderCount: Int) : ProfileViewModelUiState
    data class Error(val message: String) : ProfileViewModelUiState
}
