plugins {
    id("com.android.application")
    kotlin("android")
}

val composeVersion = "1.1.1"
val composeCompilerVersion = "1.2.0"

dependencies {
    implementation(project(":stub-android"))
    implementation(kotlin("stdlib"))
    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("androidx.appcompat:appcompat:1.4.2")

    implementation("androidx.compose.foundation:foundation-layout:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.ext["coroutinesVersion"]}")

    runtimeOnly("io.grpc:grpc-okhttp:${rootProject.ext["grpcVersion"]}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    defaultConfig {
        applicationId = "io.grpc.examples.bookinfo"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        val serverUrl: String? by project
        if (serverUrl != null) {
            resValue("string", "server_url", serverUrl!!)
        } else {
            resValue("string", "server_url", "http://192.168.108.49:50051/")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}
