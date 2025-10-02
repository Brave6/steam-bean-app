package com.coffeebean.ui.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.coffeebean.ui.theme.headlineCustom
import com.google.accompanist.pager.ExperimentalPagerApi

@Composable
fun OnboardingPageScreen(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Create refs for our components
        val (imageTitle, image, title, desc) = createRefs()

        // Image title (logo / brand image)
        Image(
            painter = painterResource(id = page.imageTitle),
            contentDescription = "Title Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(16.dp)
                .constrainAs(imageTitle) {
                    top.linkTo(parent.top, margin = 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Image(
            painter = painterResource(id = page.image),
            contentDescription = page.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(width = 397.dp, height = 303.dp)
                .constrainAs(image) {
                    top.linkTo(imageTitle.bottom, margin = 57.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineCustom, // or your custom
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(image.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(desc) {
                top.linkTo(title.bottom, margin = 38.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}
@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun OnboardingPagePreview() {
    OnboardingScreen(
        onSignupClick = { /* preview signup */ },
        onLoginClick = { /* preview login */ }
    )}
