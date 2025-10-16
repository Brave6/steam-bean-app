package com.coffeebean.ui.main.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.coffeebean.R

data class BottomNavItem(val route: String, val icon: Int, val label: String)

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", R.drawable.icon_home, "Home"),
        BottomNavItem("menu", R.drawable.icon_menu, "Menu"),
        BottomNavItem("Cart", R.drawable.icon_cart, "Cart"),
        BottomNavItem("rewards", R.drawable.icon_rewards, "Rewards"),
        BottomNavItem("account", R.drawable.icon_person, "Account")
    )

    NavigationBar(containerColor = Color(0xFFEDE7F6)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF532D6D),
                    unselectedIconColor = Color.LightGray,
                    selectedTextColor = Color(0xFF532D6D),
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFC5CAE9)
                )
            )
        }
    }
}
