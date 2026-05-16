import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()

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

    jvm {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":benchmark-kmp-common"))
            implementation(libs.koin.core)
        }
    }
}
