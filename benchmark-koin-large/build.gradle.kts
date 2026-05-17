import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    @Suppress("DEPRECATION")
    androidLibrary {
        namespace = "com.codeint.shopapp.koin"
        compileSdk = 36
        minSdk = 24
    }

    val xcf = XCFramework("BenchmarkKoin")
    iosArm64 {
        binaries.framework {
            baseName = "BenchmarkKoin"
            isStatic = true
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "BenchmarkKoin"
            isStatic = true
            xcf.add(this)
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            implementation(libs.koin.core)
            implementation("io.insert-koin:koin-core-viewmodel:4.2.0")

        }
        androidMain.dependencies {
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
        }
    }
}
