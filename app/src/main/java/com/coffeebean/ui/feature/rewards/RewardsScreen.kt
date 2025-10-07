package com.coffeebean.ui.feature.rewards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coffeebean.R
import com.coffeebean.ui.feature.menu.MenuCategorySection
import com.coffeebean.ui.feature.menu.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    navController: NavHostController,
    onItemClick: (String) -> Unit // callback when user taps a menu item
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Menu", style = MaterialTheme.typography.titleLarge) }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "All Beverages",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                MenuCategorySection(
                    title = "Coffee",
                    items = listOf(
                        MenuItem("Cappuccino", "with Chocolate", 125.00, R.drawable.carousel_1),
                        MenuItem("Cold Brew", "with Milk Foam", 145.00, R.drawable.cold_brew),
                        MenuItem("Espresso", "strong shot", 110.00, R.drawable.carousel_1)
                    ),
                    onItemClick = onItemClick
                )

                MenuCategorySection(
                    title = "Tea",
                    items = listOf(
                        MenuItem("Matcha Latte", "hot or iced", 130.00, R.drawable.carousel_1),
                        MenuItem(
                            "English Breakfast",
                            "classic black tea",
                            120.00,
                            R.drawable.carousel_1
                        )
                    ),
                    onItemClick = onItemClick
                )
            }
        }
    )
}
