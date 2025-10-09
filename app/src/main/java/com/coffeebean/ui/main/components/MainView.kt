package com.coffeebean.ui.main.components

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coffeebean.ui.feature.account.AccountsScreen
import com.coffeebean.ui.feature.home.HomeScreen
import com.coffeebean.ui.feature.menu.MenuScreen
import com.coffeebean.ui.feature.rewards.RewardsScreen
import com.coffeebean.ui.main.components.BottomBar
import com.coffeebean.ui.navigation.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainView(appNavController: NavHostController) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    onLogout = {
                        appNavController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Main.route) { inclusive = true } // remove Main from back stack
                        }
                    }
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
                    navController = navController
                )
            }
        }
    }
}
