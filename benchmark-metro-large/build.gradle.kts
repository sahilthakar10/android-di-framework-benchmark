import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.kotlin.multiplatform.library")
    alias(libs.plugins.metro)
}

kotlin {
    androidLibrary {
        namespace = "com.codeint.shopapp.metro"
        compileSdk = 36
        minSdk = 24
    }

    val xcf = XCFramework("BenchmarkMetro")
    iosX64 {
        binaries.framework {
            baseName = "BenchmarkMetro"
            isStatic = true
            xcf.add(this)
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = "BenchmarkMetro"
            isStatic = true
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "BenchmarkMetro"
            isStatic = true
            xcf.add(this)
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        }
        androidMain.dependencies {
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
        }
    }
}
