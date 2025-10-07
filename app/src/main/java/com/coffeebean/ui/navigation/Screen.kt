package com.coffeebean.ui.navigation

sealed class Screen(val route: String) {
    // Auth flow
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Signup : Screen("signup")

    // Main app
    data object Home : Screen("home")
    data object Menu : Screen("menu")
    data object Rewards : Screen("rewards")
    data object Account : Screen("account")

    // If you ever need arguments
    data class Details(val productId: String) : Screen("details/{productId}") {
        companion object {
            fun createRoute(productId: String) = "details/$productId"
        }
    }
}
