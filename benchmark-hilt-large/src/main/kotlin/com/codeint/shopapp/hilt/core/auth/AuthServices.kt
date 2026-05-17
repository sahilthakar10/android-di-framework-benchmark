package com.codeint.shopapp.hilt.core.auth

import com.codeint.shopapp.hilt.core.analytics.AnalyticsTracker
import com.codeint.shopapp.hilt.core.storage.PreferencesManager
import com.codeint.shopapp.hilt.core.storage.SecureStorage
import com.codeint.shopapp.hilt.core.network.HttpClient
import com.codeint.shopapp.hilt.core.network.TokenProvider
import javax.inject.Inject

interface AuthManager {
    fun login(email: String, password: String): Boolean
    fun logout()
    fun isLoggedIn(): Boolean
}

interface TokenStorage {
    fun saveTokens(access: String, refresh: String)
    fun getAccessToken(): String?
    fun hasValidToken(): Boolean
    fun clear()
}

interface SessionManager {
    fun startSession(userId: String)
    fun getCurrentUserId(): String?
    fun invalidate()
}

class RealAuthManager @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
    private val analyticsTracker: AnalyticsTracker
) : AuthManager {
    override fun login(email: String, password: String): Boolean { analyticsTracker.track("login_attempt"); return true }
    override fun logout() { tokenStorage.clear(); sessionManager.invalidate(); analyticsTracker.track("logout") }
    override fun isLoggedIn() = tokenStorage.hasValidToken()
}

class RealTokenStorage @Inject constructor(private val secureStorage: SecureStorage) : TokenStorage {
    override fun saveTokens(access: String, refresh: String) { secureStorage.put("access_token", access) }
    override fun getAccessToken() = secureStorage.get("access_token")
    override fun hasValidToken() = getAccessToken() != null
    override fun clear() { secureStorage.remove("access_token") }
}

class RealSessionManager @Inject constructor(private val prefs: PreferencesManager) : SessionManager {
    override fun startSession(userId: String) { prefs.putString("current_user", userId) }
    override fun getCurrentUserId() = prefs.getString("current_user")
    override fun invalidate() { prefs.remove("current_user") }
}

class BiometricAuthProvider @Inject constructor(private val secureStorage: SecureStorage) {
    fun isAvailable() = true
    fun authenticate() = true
}

class OAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {
    fun authorizeWithGoogle() = true
    fun authorizeWithApple() = true
}

class PasswordValidator @Inject constructor() {
    fun validate(password: String) = password.length >= 8
    fun getStrength(password: String) = when { password.length >= 12 -> 3; password.length >= 8 -> 2; else -> 1 }
}

class TwoFactorAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {
    fun requestCode(phone: String) = true
    fun verifyCode(code: String) = code == "123456"
}
