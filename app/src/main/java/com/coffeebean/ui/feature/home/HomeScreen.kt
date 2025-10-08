package com.coffeebean.ui.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coffeebean.R
import com.coffeebean.data.local.repository.FakeProductRepository
import com.coffeebean.domain.model.Product
import com.coffeebean.ui.feature.home.components.ProductCard
import com.coffeebean.ui.feature.home.components.PromoCarousel
import com.coffeebean.ui.theme.Recolleta
import com.coffeebean.ui.theme.coffeebeanBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    onProfileClick: () -> Unit = {},
    onLogout: () -> Unit

) {
    var searchText by remember { mutableStateOf("") }
    val products by viewModel.products.collectAsState()
    val showDialog = viewModel.showLogoutDialog.collectAsState()

    // Intercept back button
    BackHandler(enabled = true) {
        viewModel.onBackPressed()
    }
    val promoList = listOf(
        R.drawable.promo_bpi,
        R.drawable.promo_toys,
        R.drawable.promo_combo,
        R.drawable.promo_teacher_day
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hello, Seth!",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Recolleta
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
            /*
            HomeSearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onSearch = { query ->
                    // Handle search action
                },

                )

             */

            Spacer(modifier = Modifier.height(12.dp))


            // Promo Carousel
            PromoCarousel(promoImages = promoList)
/*
            // Categories Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Categories",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Recolleta,
                    fontSize = 28.sp,
                    color = coffeebeanBlack
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
 */

            Spacer(Modifier.height(16.dp))

            // Popular Now Section
            Text(
                "Popular Now",
                fontWeight = FontWeight.Bold,
                fontFamily = Recolleta,
                fontSize = 28.sp,
                color = coffeebeanBlack,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
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
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLogoutDialog() },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.confirmLogout()
                    onLogout() // Navigate back to Login screen
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.dismissLogoutDialog() }) {
                    Text("No")
                }
            }
        )
    }
}
/*
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
        onProfileClick = {},
        onLogout = {}
    )
}

 */

