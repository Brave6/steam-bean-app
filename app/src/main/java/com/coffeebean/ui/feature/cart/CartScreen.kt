package com.coffeebean.ui.feature.cart

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.coffeebean.ui.theme.Recolleta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Cart",
                        fontFamily = Recolleta,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color(0xFF532D6D),
                    titleContentColor = Color(0xFF532D6D)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF532D6D)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add some items from the menu!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF532D6D)
                    )
                ) {
                    Text("Browse Menu")
                }
            }
        }
    }
}