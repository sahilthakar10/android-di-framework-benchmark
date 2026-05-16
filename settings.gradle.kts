pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BenchMarking"

// App
include(":app")

// SDK modules
include(":di-benchmark-annotations")
include(":di-benchmark-runtime")
include(":di-benchmark-export")
include(":di-benchmark-ui")

// Gradle plugin (composite build)
includeBuild("di-benchmark-compiler")

// Sample modules
include(":sample-hilt-module")
include(":sample-metro-module")

// Large-scale compile-time benchmark modules (500 singletons + 200 factories each)
include(":benchmark-hilt-large")
include(":benchmark-metro-large")
include(":benchmark-koin-large")

// KMP iOS benchmark modules
include(":benchmark-kmp-common")
include(":benchmark-kmp-metro")
include(":benchmark-kmp-koin")
