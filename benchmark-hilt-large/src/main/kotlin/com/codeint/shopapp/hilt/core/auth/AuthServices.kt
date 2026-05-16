package com.codeint.shopapp.hilt.core.auth

import com.codeint.shopapp.hilt.core.network.*
import com.codeint.shopapp.hilt.core.storage.*
import com.codeint.shopapp.hilt.core.analytics.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class AuthManager @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
    private val analyticsTracker: AnalyticsTracker
) {
    fun login(email: String, password: String): Boolean { analyticsTracker.track("login_attempt"); return true }
    fun logout() { tokenStorage.clear(); sessionManager.invalidate(); analyticsTracker.track("logout") }
    fun isLoggedIn(): Boolean = tokenStorage.hasValidToken()
}

@Singleton class TokenStorage @Inject constructor(private val secureStorage: SecureStorage) {
    fun saveTokens(access: String, refresh: String) { secureStorage.put("access_token", access); secureStorage.put("refresh_token", refresh) }
    fun getAccessToken(): String? = secureStorage.get("access_token")
    fun hasValidToken(): Boolean = getAccessToken() != null
    fun clear() { secureStorage.remove("access_token"); secureStorage.remove("refresh_token") }
}

@Singleton class SessionManager @Inject constructor(private val preferencesManager: PreferencesManager) {
    fun startSession(userId: String) { preferencesManager.putString("current_user", userId) }
    fun getCurrentUserId(): String? = preferencesManager.getString("current_user")
    fun invalidate() { preferencesManager.remove("current_user") }
}

@Singleton class BiometricAuthProvider @Inject constructor(private val secureStorage: SecureStorage) {
    fun isAvailable(): Boolean = true
    fun authenticate(): Boolean = true
}

@Singleton class OAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {
    fun authorizeWithGoogle(): Boolean = true
    fun authorizeWithApple(): Boolean = true
    fun authorizeWithFacebook(): Boolean = true
}

@Singleton class PasswordValidator @Inject constructor() {
    fun validate(password: String): Boolean = password.length >= 8
    fun getStrength(password: String): Int = when { password.length >= 12 -> 3; password.length >= 8 -> 2; else -> 1 }
}

@Singleton class TwoFactorAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) {
    fun requestCode(phone: String): Boolean = true
    fun verifyCode(code: String): Boolean = code == "123456"
}
