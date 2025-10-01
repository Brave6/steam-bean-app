package com.coffeebean.ui.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coffeebean.ui.theme.headlineCustom
import com.google.accompanist.pager.ExperimentalPagerApi

@Composable
fun OnboardingPageScreen(
    page: OnboardingPage,   // The data for this screen
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = page.image),
            contentDescription = page.title,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)   // take available space
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineCustom,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f) // medium emphasis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun OnboardingPagePreview() {
    OnboardingScreen(onFinish = { })
}
