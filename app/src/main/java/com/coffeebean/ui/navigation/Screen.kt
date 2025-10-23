package com.coffeebean.ui.navigation

sealed class Screen(val route: String) {
    // Auth flow
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Signup : Screen("signup")

    // Main app
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Menu : Screen("menu")
    data object Rewards : Screen("rewards")
    data object Account : Screen("account")

    // Feature screens
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    data object Search : Screen("search")
    data object Cart : Screen("cart")
    data object Favorites : Screen("favorites")
    data object Checkout : Screen("checkout")


    data object OrderSuccess : Screen("order_success/{orderId}") {
        fun createRoute(orderId: String) = "order_success/$orderId"
    }

    // If you ever need arguments
    data class Details(val productId: String) : Screen("details/{productId}") {
        companion object {
            fun createRoute(productId: String) = "details/$productId"
        }
    }
}
