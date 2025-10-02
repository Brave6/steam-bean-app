package com.coffeebean.ui.feature.onboarding

import androidx.annotation.DrawableRes

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageTitle: Int,
    @DrawableRes val image: Int
)
