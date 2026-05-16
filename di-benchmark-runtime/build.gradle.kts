plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.codeint.dibenchmark.runtime"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":di-benchmark-annotations"))
    implementation(libs.androidx.core.ktx)
}
