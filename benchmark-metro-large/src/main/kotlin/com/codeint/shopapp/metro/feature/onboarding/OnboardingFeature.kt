package com.codeint.shopapp.metro.feature.onboarding

import com.codeint.shopapp.metro.core.auth.*
import com.codeint.shopapp.metro.core.analytics.AnalyticsTracker
import com.codeint.shopapp.metro.core.config.FeatureFlagManager
import dev.zacsweers.metro.Inject

class OnboardingViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val oAuthManager: OAuthManager,
    private val analytics: AnalyticsTracker,
    private val featureFlags: FeatureFlagManager
) {
    fun login(email: String, password: String): Boolean { analytics.track("login"); return authManager.login(email, password) }
    fun signInWithGoogle(): Boolean { analytics.track("google_login"); return oAuthManager.authorizeWithGoogle() }
    fun signInWithApple(): Boolean { analytics.track("apple_login"); return oAuthManager.authorizeWithApple() }
    fun isOnboardingComplete(): Boolean = authManager.isLoggedIn()
}

class RegistrationPresenter @Inject constructor(
    private val passwordValidator: PasswordValidator,
    private val analytics: AnalyticsTracker
) {
    fun validateRegistration(email: String, password: String, confirmPassword: String): List<String> {
        val errors = mutableListOf<String>()
        if (!email.contains("@")) errors.add("Invalid email")
        if (!passwordValidator.validate(password)) errors.add("Password too weak")
        if (password != confirmPassword) errors.add("Passwords don't match")
        return errors
    }
}
