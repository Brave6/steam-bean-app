plugins {
    // Core Android plugins
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Hilt for Dependency Injection
    alias(libs.plugins.hilt)

    // Firebase Google Services
    alias(libs.plugins.google.services)

    // Kotlin Annotation Processing (needed for kapt)
    id("org.jetbrains.kotlin.kapt")

    // Kotlin Serialization
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.coffeebean"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.coffeebean"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- AndroidX Core ---
    implementation(libs.androidx.core.ktx) // Core KTX utilities
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle + Coroutines integration
    implementation(libs.androidx.activity.compose) // Compose support in Activity

    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM (version alignment)
    implementation(libs.androidx.ui) // Core UI components
    implementation(libs.androidx.ui.graphics) // Graphics utilities
    implementation(libs.androidx.ui.tooling.preview) // Preview support
    implementation(libs.androidx.material3) // Material 3 design system
    implementation(libs.compose.icons)

    //Constraint Layout
    implementation(libs.constraintlayout.compose)

    //Pager
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    //Animation
    implementation(libs.accompanist.navigation.animation)

    // --- Navigation ---
    implementation(libs.androidx.navigation.compose) // Navigation for Compose

    // --- Dependency Injection (Hilt) ---
    implementation(libs.hilt.android) // Hilt runtime
    kapt(libs.hilt.compiler) // Hilt annotation processor
    implementation(libs.hilt.navigation.compose) // Hilt + Compose integration

    // --- DataStore ---
    implementation(libs.androidx.datastore.preferences) // DataStore (preferences replacement for SharedPreferences)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.android) // Kotlin coroutines for async work

    // --- Firebase ---
    implementation(platform(libs.firebase.bom)) // Firebase BoM (manages versions)
    implementation(libs.firebase.auth) // Firebase Authentication
    implementation(libs.firebase.firestore) // Firebase Firestore Database
    implementation(libs.firebase.storage) // Firebase Cloud Storage
    implementation(libs.firebase.analytics) // Firebase Analytics
    implementation(libs.play.services.auth)

    // --- Image Loading ---
    implementation(libs.coil.compose)

    // --- Testing ---
    testImplementation(libs.junit) // Unit testing
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit
    androidTestImplementation(libs.androidx.espresso.core) // Espresso UI testing
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM for tests
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose UI testing

    // --- Debugging tools ---
    debugImplementation(libs.androidx.ui.tooling) // UI tooling (Preview)
    debugImplementation(libs.androidx.ui.test.manifest) // Test manifest for Compose UI

}
