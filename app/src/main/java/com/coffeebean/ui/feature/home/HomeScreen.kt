package com.coffeebean.ui.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coffeebean.R
import com.coffeebean.data.local.repository.FakeProductRepository
import com.coffeebean.domain.model.Product
import com.coffeebean.ui.feature.home.components.CategoryItem
import com.coffeebean.ui.feature.home.components.HomeSearchBar
import com.coffeebean.ui.feature.home.components.ProductCard
import com.coffeebean.ui.theme.coffeebeanPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    onProfileClick: () -> Unit = {},
) {
    var searchText by remember { mutableStateOf("") }
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
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()

    // Sample product list for preview
    val sampleProducts = listOf(
        Product(id = 1, name = "Cappuccino", description = "With chocolate", price = "125.00", imageRes = R.drawable.carousel_1),
        Product(id = 2, name = "Cold Brew", description = "With milk foam", price = "145.00", imageRes = R.drawable.cold_brew),
        Product(id = 3, name = "Espresso", description = "Strong shot", price = "110.00", imageRes = R.drawable.carousel_1)
    )

    // Use a fake ViewModel for preview
    val fakeViewModel = object : HomeViewModel(
        productRepository = FakeProductRepository(sampleProducts)
    ) {}

    // Call the HomeScreen manually without Hilt
    HomeScreen(
        navController = navController,
        viewModel = fakeViewModel,
        onProfileClick = {}
    )
}

