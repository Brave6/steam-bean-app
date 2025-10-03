package com.coffeebean.ui.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    //onFinish: () -> Unit,
    onSignupClick: () -> Unit,   // for Get Started
    onLoginClick: () -> Unit     // for "Already have an account? Login"

) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))


    ) {
        val (pager, indicator, button, login) = createRefs()

        // Pager (takes top portion)
        HorizontalPager(
            count = onboardingPages.size,
            state = pagerState,
            modifier = Modifier.constrainAs(pager) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(indicator.top)
                height = androidx.constraintlayout.compose.Dimension.fillToConstraints
            }
        ) { page ->
            OnboardingPageScreen(page = onboardingPages[page])
        }

        // Pager indicator
        HorizontalPagerIndicator(
            indicatorWidth = 16.dp,
            indicatorHeight = 16.dp,
            pagerState = pagerState,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.constrainAs(indicator) {
                bottom.linkTo(button.top, margin = 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Main button
        Button(
            onClick = {
                if (pagerState.currentPage + 1 < onboardingPages.size) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                   // onFinish()
                    onSignupClick() // ✅ navigate to Signup

                }
            },
            modifier = Modifier
                .constrainAs(button) {
                    bottom.linkTo(login.top, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == onboardingPages.lastIndex)
                    "Get Started"
                else
                    "Next"
            )
        }

        // "Already have an account" login link
        TextButton(
           // onClick = { onFinish() },
            onClick = { onLoginClick() }, // ✅ navigate to Login
            modifier = Modifier.constrainAs(login) {
                bottom.linkTo(parent.bottom, margin = 50.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text("Already have an account? Login",
                color = Color.Black,
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun OnboardingPage() {
   // OnboardingScreen(onFinish = { })
}
