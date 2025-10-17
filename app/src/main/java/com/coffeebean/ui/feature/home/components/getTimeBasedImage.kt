package com.coffeebean.ui.feature.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.coffeebean.R

@Composable
fun getTimeBasedImage(): Int {
    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }

    return when (currentHour) {
        in 5..11 -> R.drawable.logo_cloudy      // 🌅 Morning
        in 12..17 -> R.drawable.logo_cloudy         // 🌞 Noon
        else -> R.drawable.logo_moon           // 🌙 Evening
    }
}
