plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    compileOnly("com.android.tools.build:gradle:9.2.0")
}

gradlePlugin {
    plugins {
        create("diBenchmark") {
            id = "com.codeint.dibenchmark"
            implementationClass = "com.codeint.dibenchmark.gradle.DiBenchmarkPlugin"
        }
    }
}
