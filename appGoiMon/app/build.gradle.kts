import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.appgoimon"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.appgoimon"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read base URL from local.properties with a fallback that works for the Android emulator.
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        val baseUrl = localProperties.getProperty("base.url")
            ?: "http://10.0.2.2/appOrder/appGoiMon_API/"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val configuredBaseUrl = localProperties.getProperty("base.url")
    ?: "http://10.0.2.2/appOrder/appGoiMon_API/"

val configureUsbApiReverse by tasks.registering {
    group = "android"
    description = "Maps the Android device API port to the local development server for USB debugging."

    onlyIf {
        localProperties.getProperty("api.reverse.enabled", "true").toBoolean()
            && configuredBaseUrl.contains("://127.0.0.1:")
    }

    doLast {
        val sdkDir = localProperties.getProperty("sdk.dir")
            ?: System.getenv("ANDROID_HOME")
            ?: System.getenv("ANDROID_SDK_ROOT")
            ?: error("Android SDK path not found. Set sdk.dir in local.properties.")

        val adbExecutable = file("$sdkDir/platform-tools/adb.exe").takeIf { it.exists() }
            ?: file("$sdkDir/platform-tools/adb").takeIf { it.exists() }
            ?: error("adb not found under $sdkDir/platform-tools.")

        val devicePort = localProperties.getProperty("api.reverse.devicePort", "8080")
        val hostPort = localProperties.getProperty("api.reverse.hostPort", "80")
        val result = providers.exec {
            commandLine(adbExecutable.absolutePath, "reverse", "tcp:$devicePort", "tcp:$hostPort")
            isIgnoreExitValue = true
        }.result.get()

        if (result.exitValue != 0) {
            logger.warn("Failed to configure adb reverse tcp:$devicePort -> tcp:$hostPort. Check USB debugging and device connection.")
        } else {
            logger.lifecycle("Configured adb reverse tcp:$devicePort -> tcp:$hostPort for USB API access.")
        }
    }
}

tasks.matching { it.name == "installDebug" || it.name == "preDebugBuild" }.configureEach {
    dependsOn(configureUsbApiReverse)
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
