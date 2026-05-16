package com.codeint.shopapp.koin.feature.profile

import com.codeint.shopapp.koin.domain.user.*
import com.codeint.shopapp.koin.domain.order.*
import com.codeint.shopapp.koin.domain.address.*
import com.codeint.shopapp.koin.core.auth.AuthManager
import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.image.ImageLoader


class ProfileViewModel constructor(
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

class AddressManagerPresenter constructor(
    private val getAddressList: GetAddressListUseCase,
    private val createAddress: CreateAddressUseCase,
    private val deleteAddress: DeleteAddressUseCase
) {
    fun getAddresses(): List<AddressDomainModel> = getAddressList.execute().items
    fun addAddress(address: AddressDomainModel) { createAddress.execute(address) }
    fun removeAddress(id: String) { deleteAddress.execute(id) }
}

class AccountSecurityPresenter constructor(
    private val authManager: AuthManager,
    private val twoFactor: com.codeint.shopapp.koin.core.auth.TwoFactorAuthManager,
    private val biometric: com.codeint.shopapp.koin.core.auth.BiometricAuthProvider
) {
    fun isTwoFactorEnabled(): Boolean = true
    fun isBiometricEnabled(): Boolean = biometric.isAvailable()
    fun changePassword(current: String, new: String): Boolean = true
}

data class ProfileState(val name: String, val email: String, val orderCount: Int, val addressCount: Int)
