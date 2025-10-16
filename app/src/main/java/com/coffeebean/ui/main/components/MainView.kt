package com.coffeebean.ui.main.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.coffeebean.ui.feature.account.AccountsScreen
import com.coffeebean.ui.feature.cart.CartScreen
import com.coffeebean.ui.feature.home.HomeScreen
import com.coffeebean.ui.feature.menu.MenuScreen
import com.coffeebean.ui.feature.menu.components.product.ProductDetailScreen
import com.coffeebean.ui.feature.rewards.RewardsScreen
import com.coffeebean.ui.navigation.Screen

@Composable
fun MainView(appNavController: NavHostController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val onLogout = {
        appNavController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    // List of routes where bottom bar should be hidden
    val hideBottomBarRoutes = listOf(
        "product_detail/{productId}",
        "search",
        "cart"
    )

    val shouldShowBottomBar = currentRoute !in hideBottomBarRoutes &&
            !currentRoute.orEmpty().startsWith("product_detail/")

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        navController = navController,
                        onLogout = onLogout
                    )
                }
                composable(Screen.Menu.route) {
                    MenuScreen(
                        navController = navController,
                        onItemClick = { menuItem ->
                            // 🔥 Navigate to product detail!
                            navController.navigate(
                                Screen.ProductDetail.createRoute(menuItem.id)
                            )
                        }
                    )
                }
                composable(Screen.Rewards.route) {
                    RewardsScreen(
                        navController = navController
                    )
                }
                composable(Screen.Account.route) {
                    AccountsScreen(
                        navController = navController,
                        onLogout = onLogout
                    )
                }

            // 🔥 Add this composable block
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""

                ProductDetailScreen(
                    productId = productId,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToCart = { /* ... */ }
                )
            }

            // Cart Screen
            composable(
                route = Screen.Cart.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                CartScreen(
                    navController = navController,
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(
                            Screen.ProductDetail.createRoute(productId)
                        )
                    },
                    onCheckout = {
                        // Navigate to checkout when implemented
                    }
                )
            }

        }
    }
}