package com.coffeebean.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.coffeebean.ui.main.components.MainView

@Composable
fun MainScreen(appNavController: NavHostController) {
    MainView(appNavController = appNavController)
}
