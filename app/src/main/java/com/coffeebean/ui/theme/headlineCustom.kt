package com.coffeebean.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

val Typography.headlineCustom: TextStyle
    get() = TextStyle(
        fontFamily = FontFamily.Default, // or FontFamily(Font(R.font.roboto_regular))
        fontWeight = FontWeight.SemiBold,
        color = coffeebeanPurple,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )
