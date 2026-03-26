import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.aboutlibraries)
}

val localProperties = Properties()
rootProject.file("local.properties").let { file ->
    if (file.exists()) FileInputStream(file).use { localProperties.load(it) }
}

fun prop(key: String): String =
    System.getenv(key)?.takeIf { it.isNotEmpty() }
        ?: localProperties.getProperty(key, "")

val majorVersion: Int = (findProperty("MAJOR_VERSION") as? String)?.toIntOrNull() ?: 1
val buildNumber: Int = (findProperty("BUILD_NUMBER") as? String)?.toIntOrNull() ?: 1
val buildNumberPadded: String = "%03d".format(buildNumber)

android {
    namespace = "com.ieshaan12.metronome"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.ieshaan12.metronome"
        minSdk = 29
        targetSdk = 36
        versionName = "$majorVersion.$buildNumberPadded"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val storePath = prop("RELEASE_STORE_FILE")
            storeFile = if (storePath.isNotEmpty()) file(storePath) else null
            storePassword = prop("RELEASE_STORE_PASSWORD")
            keyAlias = prop("RELEASE_KEY_ALIAS")
            keyPassword = prop("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    flavorDimensions += "track"
    productFlavors {
        create("dev") {
            dimension = "track"
            versionCode = majorVersion * 10000 + buildNumber * 10 + 0
            versionNameSuffix = ".00"
            buildConfigField("String", "TRACK", "\"dev\"")
        }
        create("production") {
            dimension = "track"
            versionCode = majorVersion * 10000 + buildNumber * 10 + 1
            versionNameSuffix = ".01"
            buildConfigField("String", "TRACK", "\"production\"")
        }
        create("closedTesting") {
            dimension = "track"
            versionCode = majorVersion * 10000 + buildNumber * 10 + 2
            versionNameSuffix = ".02"
            buildConfigField("String", "TRACK", "\"closedTesting\"")
        }
        create("internalTesting") {
            dimension = "track"
            versionCode = majorVersion * 10000 + buildNumber * 10 + 3
            versionNameSuffix = ".03"
            buildConfigField("String", "TRACK", "\"internalTesting\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        lintConfig = file("lint.xml")
        abortOnError = true
        htmlReport = true
    }
    packaging {
        resources {
            excludes += listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.aboutlibraries.compose)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.benchmark.junit4)
    androidTestImplementation(libs.leakcanary.instrumentation)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.leakcanary)
    debugImplementation(libs.androidx.compose.runtime.tracing)
}