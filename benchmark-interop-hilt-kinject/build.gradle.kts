plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.codeint.interop.kinject"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Shares Hilt infrastructure from Metro interop module
    implementation(project(":benchmark-interop-hilt-metro"))
    implementation(libs.kotlin.inject.runtime)
    ksp(libs.kotlin.inject.compiler)
}
