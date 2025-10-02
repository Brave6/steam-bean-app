package com.coffeebean.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coffeebean.ui.feature.onboarding.OnboardingScreen
import com.coffeebean.ui.feature.signup.SignupScreen
import com.coffeebean.ui.feature.splash.SplashScreen

//import com.coffeebean.ui.feature.onboarding.OnboardingScreen
//import com.coffeebean.ui.feature.login.LoginScreen
//import com.coffeebean.ui.feature.home.HomeScreen

object Destinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"

    const val SIGNUP = "signup"
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
            OnboardingScreen(
                onSignupClick = {
                    navController.navigate(Destinations.SIGNUP) {
                        popUpTo(Destinations.SPLASH) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(Destinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }


        composable(Destinations.SIGNUP) {
            SignupScreen(
                onSignUpClick = {
                    // after successful signup, you may go to login or home
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(Destinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }


        /*
                composable(Destinations.LOGIN) {
                    LoginScreen(navController)
                }
                composable(Destinations.HOME) {
                    HomeScreen()
                }

         */


    }
}
