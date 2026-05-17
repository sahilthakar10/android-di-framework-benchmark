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

// Sample modules
include(":sample-hilt-module")
include(":sample-metro-module")

// Large-scale compile-time benchmark modules (500 singletons + 200 factories each)
include(":benchmark-hilt-large")
include(":benchmark-metro-large")
include(":benchmark-koin-large")

// Interop demos: Hilt + each framework (50% Hilt infra, 50% framework features)
include(":benchmark-interop-hilt-metro")
include(":benchmark-interop-hilt-koin")
include(":benchmark-interop-hilt-kinject")

