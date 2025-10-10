package com.coffeebean.ui.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.coffeebean.R
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
    val isLoggingOut by viewModel.isLoggingOut.collectAsStateWithLifecycle()

    // Handle logout navigation
    LaunchedEffect(isLoggingOut) {
        if (isLoggingOut) {
            onLogout()
        }
    }

    // Intercept back button
    BackHandler(enabled = true) {
        viewModel.onBackPressed()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hello, Seth!",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Recolleta
                    )
                },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make entire content scrollable
                .padding(
                    top = innerPadding.calculateTopPadding(),
                   // bottom = 80.dp // Account for bottom nav bar
                )
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Promo Carousel Section
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
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

            // Popular Now Section
            Text(
                "Popular Now",
                fontWeight = FontWeight.Bold,
                fontFamily = Recolleta,
                fontSize = 28.sp,
                color = coffeebeanBlack,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Products Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), // Important: Let it take natural height
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    ProductCard(product)
                }
            }

            // Bottom spacer for additional breathing room
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Logout Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLogoutDialog() },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.confirmLogout()
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