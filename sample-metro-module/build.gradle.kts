plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.metro)
}

android {
    namespace = "com.codeint.sample.metro"
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
    debugImplementation(project(":di-benchmark-runtime"))
    implementation(project(":di-benchmark-annotations"))
}
