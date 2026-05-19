package com.codeint.shopapp.kinject.core.auth

import com.codeint.shopapp.kinject.core.analytics.AnalyticsTracker
import com.codeint.shopapp.kinject.core.storage.PreferencesManager
import com.codeint.shopapp.kinject.core.storage.SecureStorage
import com.codeint.shopapp.kinject.core.network.HttpClient
import com.codeint.shopapp.kinject.core.network.TokenProvider
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

interface AuthManager { fun login(email: String, password: String): Boolean; fun logout(); fun isLoggedIn(): Boolean }
interface TokenStorage { fun saveTokens(a: String, r: String); fun getAccessToken(): String?; fun hasValidToken(): Boolean; fun clear() }
interface SessionManager { fun startSession(userId: String); fun getCurrentUserId(): String?; fun invalidate() }

@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealAuthManager(private val tokenStorage: TokenStorage, private val sessionManager: SessionManager, private val analytics: AnalyticsTracker) : AuthManager {
    override fun login(email: String, password: String): Boolean { analytics.track("login"); return true }
    override fun logout() { tokenStorage.clear(); sessionManager.invalidate() }; override fun isLoggedIn() = tokenStorage.hasValidToken()
}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealTokenStorage(private val secureStorage: SecureStorage) : TokenStorage {
    override fun saveTokens(a: String, r: String) { secureStorage.put("token", a) }; override fun getAccessToken() = secureStorage.get("token")
    override fun hasValidToken() = getAccessToken() != null; override fun clear() { secureStorage.remove("token") }
}
@Inject @SingleIn(AppScope::class) @ContributesBinding(AppScope::class) class RealSessionManager(private val prefs: PreferencesManager) : SessionManager {
    override fun startSession(userId: String) { prefs.putString("user", userId) }; override fun getCurrentUserId() = prefs.getString("user"); override fun invalidate() { prefs.remove("user") }
}
@Inject class BiometricAuthProvider(private val secureStorage: SecureStorage) { fun isAvailable() = true }
@Inject class OAuthManager(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) { fun authorizeWithGoogle() = true }
@Inject class PasswordValidator { fun validate(password: String) = password.length >= 8 }
@Inject class TwoFactorAuthManager(private val httpClient: HttpClient, private val tokenStorage: TokenStorage) { fun requestCode(phone: String) = true }
