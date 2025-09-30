package com.coffeebean.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coffeebean.ui.feature.splash.SplashScreen
import com.coffeebean.ui.feature.onboarding.OnboardingScreen
import com.coffeebean.ui.feature.login.LoginScreen
import com.coffeebean.ui.feature.home.HomeScreen

object Destinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val HOME = "home"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.SPLASH,
        modifier = modifier
    ) {
        composable(Destinations.SPLASH) {
            SplashScreen(navController)
        }
        composable(Destinations.ONBOARDING) {
            OnboardingScreen(navController)
        }
        composable(Destinations.LOGIN) {
            LoginScreen(navController)
        }
        composable(Destinations.HOME) {
            HomeScreen()
        }
    }
}
