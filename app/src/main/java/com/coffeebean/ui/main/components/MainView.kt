package com.coffeebean.ui.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coffeebean.ui.feature.account.AccountsScreen
import com.coffeebean.ui.feature.home.HomeScreen
import com.coffeebean.ui.feature.menu.MenuScreen
import com.coffeebean.ui.feature.rewards.RewardsScreen
import com.coffeebean.ui.navigation.Screen

@Composable
fun MainView(appNavController: NavHostController) {
    val navController = rememberNavController()

    val onLogout = {
        appNavController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

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
                        onItemClick = { productName ->
                            println("Clicked on $productName")
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
        }
    }
}