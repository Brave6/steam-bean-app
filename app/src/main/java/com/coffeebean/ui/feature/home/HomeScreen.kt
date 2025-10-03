package com.coffeebean.ui.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.coffeebean.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hello, Seth", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF532D6D))
            {

                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = { selectedTab = "home"; onNavigate("home") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_home),
                            contentDescription = "Home"
                        )
                    },                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF607D8B),      // custom color when selected
                        unselectedIconColor = Color.LightGray,           // custom color when not selected
                        selectedTextColor = Color(0xFFC6C8CA),      // text color when selected
                        unselectedTextColor = Color.White,           // text color when not selected
                        indicatorColor = Color(0xFFC5CAE9)          // background highlight when selected
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == "menu",
                    onClick = { selectedTab = "menu"; onNavigate("menu") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_menu),
                            contentDescription = "Menu"
                        )
                    },
                    label = { Text("Menu") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF607D8B),      // custom color when selected
                        unselectedIconColor = Color.LightGray,           // custom color when not selected
                        selectedTextColor = Color(0xFFC6C8CA),      // text color when selected
                        unselectedTextColor = Color.White,           // text color when not selected
                        indicatorColor = Color(0xFFC5CAE9)          // background highlight when selected
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == "rewards",
                    onClick = { selectedTab = "rewards"; onNavigate("rewards") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_rewards),
                            contentDescription = "Rewards"
                        )
                    },                    label = { Text("Rewards") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF607D8B),      // custom color when selected
                        unselectedIconColor = Color.LightGray,           // custom color when not selected
                        selectedTextColor = Color(0xFFC6C8CA),      // text color when selected
                        unselectedTextColor = Color.White,           // text color when not selected
                        indicatorColor = Color(0xFFC5CAE9)          // background highlight when selected
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == "account",
                    onClick = { selectedTab = "account"; onNavigate("account") },
                    icon = { Icon(Icons.Default.PersonOutline, contentDescription = "Account") },
                    label = { Text("Account") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF607D8B),      // custom color when selected
                        unselectedIconColor = Color.LightGray,           // custom color when not selected
                        selectedTextColor = Color(0xFFC6C8CA),      // text color when selected
                        unselectedTextColor = Color.White,           // text color when not selected
                        indicatorColor = Color(0xFFC5CAE9)          // background highlight when selected
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            HomeSearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onSearch = { query ->
                    // Handle search action
                },

            )

            Spacer(modifier = Modifier.height(48.dp))

            // TODO: Add home content here (cards, promos, featured items)
            Text("Dashboard Content goes here…")
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
