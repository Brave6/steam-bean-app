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

    id("io.gitlab.arturbosch.detekt") version "1.23.8"

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

    lint {
        abortOnError = true
        warningsAsErrors = true
        checkAllWarnings = true
        checkDependencies = true
        htmlReport = true
        htmlOutput = file("$buildDir/reports/lint/lint-report.html")
        baseline = file("lint-baseline.xml")
    }

    packaging {
        resources {
            // Exclude the JNDI NameResolverProvider service file that causes the Lint error.
            // This forces gRPC to use an Android-friendly resolver like OkHttp.
            excludes += "META-INF/services/io.grpc.NameResolverProvider"
        }
    }
}

detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = true
    baseline = file("$rootDir/config/detekt/baseline.xml")

    reports {
        html.required.set(true)
        html.outputLocation.set(file("$buildDir/reports/detekt/detekt.html"))
        xml.required.set(false)
        txt.required.set(false)
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

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

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

    // --- Maps ---
    implementation(libs.maps.compose)
    implementation(libs.maps.ktx)
    implementation(libs.places)
    implementation(libs.play.services.location)

    // --- Image Loading ---
    implementation(libs.coil.compose)

    // Detekt Compose
    detektPlugins(libs.detekt)

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
