package com.coffeebean.ui.feature.splash

import androidx.compose.runtime.*
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import com.coffeebean.ui.navigation.Destinations

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val completed by viewModel.onboardingCompleted.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        delay(1500) // splash delay
        if (completed) {
            navController.navigate(Destinations.LOGIN) {
                popUpTo(Destinations.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(Destinations.ONBOARDING) {
                popUpTo(Destinations.SPLASH) { inclusive = true }
            }
        }
    }
}
