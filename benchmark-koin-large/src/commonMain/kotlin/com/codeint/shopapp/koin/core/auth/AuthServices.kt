package com.codeint.shopapp.koin.core.auth

import com.codeint.shopapp.koin.core.analytics.AnalyticsTracker
import com.codeint.shopapp.koin.core.storage.PreferencesManager
import com.codeint.shopapp.koin.core.storage.SecureStorage
import com.codeint.shopapp.koin.core.network.HttpClient
import com.codeint.shopapp.koin.core.network.TokenProvider

interface AuthManager { fun login(email: String, password: String): Boolean; fun logout(); fun isLoggedIn(): Boolean }
interface TokenStorage { fun saveTokens(a: String, r: String); fun getAccessToken(): String?; fun hasValidToken(): Boolean; fun clear() }
interface SessionManager { fun startSession(userId: String); fun getCurrentUserId(): String?; fun invalidate() }

class RealAuthManager(private val tokenStorage: TokenStorage, private val sessionManager: SessionManager, private val analytics: AnalyticsTracker) : AuthManager {
    override fun login(email: String, password: String): Boolean { analytics.track("login"); return true }
    override fun logout() { tokenStorage.clear(); sessionManager.invalidate() }
    override fun isLoggedIn() = tokenStorage.hasValidToken()
}
class RealTokenStorage(private val secureStorage: SecureStorage) : TokenStorage {
    override fun saveTokens(a: String, r: String) { secureStorage.put("token", a) }
    override fun getAccessToken() = secureStorage.get("token")
    override fun hasValidToken() = getAccessToken() != null
    override fun clear() { secureStorage.remove("token") }
}
class RealSessionManager(private val prefs: PreferencesManager) : SessionManager {
    override fun startSession(userId: String) { prefs.putString("user", userId) }
    override fun getCurrentUserId() = prefs.getString("user")
    override fun invalidate() { prefs.remove("user") }
}
class BiometricAuthProvider(private val secureStorage: SecureStorage) { fun isAvailable() = true }
class OAuthManager(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) { fun authorizeWithGoogle() = true }
class PasswordValidator { fun validate(password: String) = password.length >= 8 }
class TwoFactorAuthManager(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) { fun requestCode(phone: String) = true }
