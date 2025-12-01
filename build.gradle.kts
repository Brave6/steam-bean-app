// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android + Kotlin
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Hilt plugin (applied in app module only)
    alias(libs.plugins.hilt) apply false

    // Firebase Google Services plugin (applied in app module only)
    alias(libs.plugins.google.services) apply false
}
