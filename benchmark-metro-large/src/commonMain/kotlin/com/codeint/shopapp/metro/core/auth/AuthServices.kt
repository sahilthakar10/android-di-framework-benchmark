package com.codeint.shopapp.metro.core.auth

import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.storage.PreferencesManager
import com.codeint.shopapp.metro.core.storage.SecureStorage
import com.codeint.shopapp.metro.core.network.HttpClient
import com.codeint.shopapp.metro.core.network.TokenProvider
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.AppScope

interface AuthManager { fun login(email: String, password: String): Boolean; fun logout(); fun isLoggedIn(): Boolean }
interface TokenStorage { fun saveTokens(a: String, r: String); fun getAccessToken(): String?; fun hasValidToken(): Boolean; fun clear() }
interface SessionManager { fun startSession(userId: String); fun getCurrentUserId(): String?; fun invalidate() }

@SingleIn(AppScope::class) class RealAuthManager @Inject constructor(private val tokenStorage: TokenStorage, private val sessionManager: SessionManager, private val analytics: AnalyticsTracker) : AuthManager {
    override fun login(email: String, password: String): Boolean { analytics.track("login"); return true }
    override fun logout() { tokenStorage.clear(); sessionManager.invalidate() }; override fun isLoggedIn() = tokenStorage.hasValidToken()
}
@SingleIn(AppScope::class) class RealTokenStorage @Inject constructor(private val secureStorage: SecureStorage) : TokenStorage {
    override fun saveTokens(a: String, r: String) { secureStorage.put("token", a) }; override fun getAccessToken() = secureStorage.get("token")
    override fun hasValidToken() = getAccessToken() != null; override fun clear() { secureStorage.remove("token") }
}
@SingleIn(AppScope::class) class RealSessionManager @Inject constructor(private val prefs: PreferencesManager) : SessionManager {
    override fun startSession(userId: String) { prefs.putString("user", userId) }; override fun getCurrentUserId() = prefs.getString("user"); override fun invalidate() { prefs.remove("user") }
}
class BiometricAuthProvider @Inject constructor(private val secureStorage: SecureStorage) { fun isAvailable() = true }
class OAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) { fun authorizeWithGoogle() = true }
class PasswordValidator @Inject constructor() { fun validate(password: String) = password.length >= 8 }
class TwoFactorAuthManager @Inject constructor(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) { fun requestCode(phone: String) = true }
