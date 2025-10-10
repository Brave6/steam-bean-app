package com.coffeebean.ui.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coffeebean.R
import com.coffeebean.domain.model.Product
import com.coffeebean.domain.model.Promo
import com.coffeebean.ui.feature.home.components.ProductCard
import com.coffeebean.ui.feature.home.components.PromoCarousel
import com.coffeebean.ui.theme.Recolleta
import com.coffeebean.ui.theme.coffeebeanBlack
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    onProfileClick: () -> Unit = {},
    onLogout: () -> Unit,
    analytics: FirebaseAnalytics = Firebase.analytics,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val showDialog by viewModel.showLogoutDialog.collectAsStateWithLifecycle()

    // Intercept back button
    BackHandler(enabled = true) {
        viewModel.onBackPressed()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hello, Seth!", fontWeight = FontWeight.Bold, fontFamily = Recolleta) },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(48.dp)
                    )
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color(0xFF532D6D),
                    titleContentColor = Color(0xFF532D6D),
                    actionIconContentColor = Color(0xFF532D6D)
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
            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Success -> {
                    PromoCarousel(
                        promos = state.promos,
                        onPromoClick = { promo ->
                            analytics.logEvent(FirebaseAnalytics.Event.SELECT_PROMOTION) {
                                param(FirebaseAnalytics.Param.PROMOTION_ID, promo.promotionId)
                                param(FirebaseAnalytics.Param.PROMOTION_NAME, promo.promotionName)
                                param(FirebaseAnalytics.Param.CREATIVE_NAME, promo.creativeName)
                                param(FirebaseAnalytics.Param.CREATIVE_SLOT, promo.creativeSlot)
                                param(FirebaseAnalytics.Param.LOCATION_ID, promo.locationId)
                            }
                            promo.targetScreen?.let { screen ->
                                navController.navigate(screen)
                            }
                        },
                        onPromoViewed = { promo ->
                            analytics.logEvent(FirebaseAnalytics.Event.VIEW_PROMOTION) {
                                param(FirebaseAnalytics.Param.PROMOTION_ID, promo.promotionId)
                                param(FirebaseAnalytics.Param.PROMOTION_NAME, promo.promotionName)
                                param(FirebaseAnalytics.Param.CREATIVE_NAME, promo.creativeName)
                                param(FirebaseAnalytics.Param.CREATIVE_SLOT, promo.creativeSlot)
                                param(FirebaseAnalytics.Param.LOCATION_ID, promo.locationId)
                            }
                        }
                    )
                }
                is HomeUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Popular Now",
                fontWeight = FontWeight.Bold,
                fontFamily = Recolleta,
                fontSize = 28.sp,
                color = coffeebeanBlack,
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLogoutDialog() },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.confirmLogout()
                    onLogout()
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
    val dummyPromos = listOf(
        Promo(id = "1", title = "BPI Promo", imageUrl = "https://via.placeholder.com/600x300.png/FFD700/000000?Text=BPI+Promo"),
        Promo(id = "2", title = "Toys Promo", imageUrl = "https://via.placeholder.com/600x300.png/ADD8E6/000000?Text=Toys+Promo")
    )
    val dummyProducts = listOf(
        Product(id = "1", name = "Cappuccino", description = "With chocolate", price = 125.00, imageUrl = ""),
        Product(id = "2", name = "Cold Brew", description = "With milk foam", price = 145.00, imageUrl = "")
    )


    val fakeViewModel = object : HomeViewModel(
        promoRepository = object : com.coffeebean.domain.repository.PromoRepository {
            override suspend fun getPromos(): List<Promo> = dummyPromos
        },
        productRepository = object : com.coffeebean.domain.repository.ProductRepository {
            override fun getProducts(): kotlinx.coroutines.flow.Flow<List<Product>> = kotlinx.coroutines.flow.flowOf(dummyProducts)
        },
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance()
    ) {}

    HomeScreen(
        navController = navController,
        viewModel = fakeViewModel,
        onProfileClick = {},
        onLogout = {}
    )
}

 */
