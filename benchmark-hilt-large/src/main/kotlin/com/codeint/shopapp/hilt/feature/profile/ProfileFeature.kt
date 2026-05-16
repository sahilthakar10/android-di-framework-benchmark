package com.codeint.shopapp.hilt.feature.profile

import com.codeint.shopapp.hilt.domain.user.*
import com.codeint.shopapp.hilt.domain.order.*
import com.codeint.shopapp.hilt.domain.address.*
import com.codeint.shopapp.hilt.core.auth.AuthManager
import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.image.ImageLoader
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val getUserDetail: GetUserDetailUseCase,
    private val updateUser: UpdateUserUseCase,
    private val getOrderList: GetOrderListUseCase,
    private val getAddressList: GetAddressListUseCase,
    private val authManager: AuthManager,
    private val analytics: AnalyticsTracker,
    private val imageLoader: ImageLoader
) {
    fun loadProfile(): ProfileState {
        analytics.screen("profile")
        val userId = "current_user"
        val user = getUserDetail.execute(userId)
        val recentOrders = getOrderList.execute().items.take(3)
        val addresses = getAddressList.execute().items
        return ProfileState(user?.name ?: "Guest", user?.description ?: "", recentOrders.size, addresses.size)
    }
    fun updateName(name: String) { updateUser.execute("current_user", UserDomainModel("current_user", name)) }
    fun logout() { authManager.logout(); analytics.track("logout") }
}

class AddressManagerPresenter @Inject constructor(
    private val getAddressList: GetAddressListUseCase,
    private val createAddress: CreateAddressUseCase,
    private val deleteAddress: DeleteAddressUseCase
) {
    fun getAddresses(): List<AddressDomainModel> = getAddressList.execute().items
    fun addAddress(address: AddressDomainModel) { createAddress.execute(address) }
    fun removeAddress(id: String) { deleteAddress.execute(id) }
}

class AccountSecurityPresenter @Inject constructor(
    private val authManager: AuthManager,
    private val twoFactor: com.codeint.shopapp.hilt.core.auth.TwoFactorAuthManager,
    private val biometric: com.codeint.shopapp.hilt.core.auth.BiometricAuthProvider
) {
    fun isTwoFactorEnabled(): Boolean = true
    fun isBiometricEnabled(): Boolean = biometric.isAvailable()
    fun changePassword(current: String, new: String): Boolean = true
}

data class ProfileState(val name: String, val email: String, val orderCount: Int, val addressCount: Int)
