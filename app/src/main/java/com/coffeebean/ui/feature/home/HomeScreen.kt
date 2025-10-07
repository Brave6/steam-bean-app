package com.coffeebean.ui.feature.home

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.coffeebean.R
import com.coffeebean.data.local.repository.DummyProductRepository
import com.coffeebean.domain.model.Product
import com.coffeebean.ui.theme.coffeebeanPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("home") }
    val products by viewModel.products.collectAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hello, Seth!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                ) },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo), // replace with your logo drawable
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(48.dp) // adjust size as needed
                    )
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,   // background color
                    navigationIconContentColor = Color(0xFF532D6D), // nav icon tint
                    titleContentColor = Color(0xFF532D6D),          // title text tint
                    actionIconContentColor = Color(0xFF532D6D)  // actions tint
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFEDE7F6))
            {

                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = { selectedTab = "home"; onNavigate("home") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_home),
                            contentDescription = "Home"
                        )
                    }, label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF532D6D),      // custom color when selected
                        unselectedIconColor = Color.LightGray,           // custom color when not selected
                        selectedTextColor = Color(0xFF532D6D),      // text color when selected
                        unselectedTextColor = Color.Gray,           // text color when not selected
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
                        selectedIconColor = Color(0xFF532D6D),      // custom color when selected
                        unselectedIconColor = Color.LightGray,           // custom color when not selected
                        selectedTextColor = Color(0xFF532D6D),      // text color when selected
                        unselectedTextColor = Color.Gray,           // text color when not selected
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
                    }, label = { Text("Rewards") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF532D6D),      // custom color when selected
                        unselectedIconColor = Color.LightGray,           // custom color when not selected
                        selectedTextColor = Color(0xFF532D6D),      // text color when selected
                        unselectedTextColor = Color.Gray,           // text color when not selected
                        indicatorColor = Color(0xFFC5CAE9)          // background highlight when selected
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == "account",
                    onClick = { selectedTab = "account"; onNavigate("account") },
                    icon = { Icon(Icons.Default.PersonOutline, contentDescription = "Account") },
                    label = { Text("Account") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF532D6D),      // custom color when selected
                        unselectedIconColor = Color.LightGray,           // custom color when not selected
                        selectedTextColor = Color(0xFF532D6D),      // text color when selected
                        unselectedTextColor = Color.Gray,           // text color when not selected
                        indicatorColor = Color(0xFFC5CAE9)          // background highlight when selected
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HomeSearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onSearch = { query ->
                    // Handle search action
                },

                )

            Spacer(modifier = Modifier.height(12.dp))



            // Special Offer Card
            Card(
                border = BorderStroke(1.dp, Color.LightGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF532D6D)),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Special Offer!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        "Get 20% off your next order.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { /* Claim offer */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Claim Now", color = coffeebeanPurple)
                    }
                }
            }
            // Categories Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Categories",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF532D6D)
                )
                Text("See all",
                    color = Color.Black,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CategoryItem(R.drawable.icon_coffee, "Coffee")
                CategoryItem(R.drawable.icon_tea, "Tea")
                CategoryItem(R.drawable.icon_chef, "Kitchen")
                //CategoryItem(R.drawable.icon, "Gear")
            }

            Spacer(Modifier.height(16.dp))

            // Popular Now Section
            Text(
                "Popular Now",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF532D6D),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    ProductCard(product)
                }
            }
        }
    }
}
@Composable
fun CategoryItem(iconRes: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(80.dp),
            tint = Color.Unspecified
        )
        Text(label)
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier

            .width(160.dp)
            .height(200.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Column(Modifier.padding(8.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, color = coffeebeanPurple)
                Text(product.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(product.price, fontWeight = FontWeight.Bold, color = Color(0xFFF28E6B))
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    //val fakeViewModel = HomeViewModel(DummyProductRepository())
   // HomeScreen(viewModel = fakeViewModel)
    HomeScreen()
}
