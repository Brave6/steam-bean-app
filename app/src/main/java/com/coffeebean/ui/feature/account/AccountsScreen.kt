package com.coffeebean.ui.feature.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coffeebean.ui.theme.Recolleta
import com.coffeebean.ui.theme.coffeebeanPurple

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AccountsScreen(
    navController: NavHostController,
    userName: String = "Seth Aldwin Tolentino",
    email: String = "seth@example.com",
    loyaltyPoints: Int = 320,
    loyaltyTier: String = "Gold"
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Account",
                    fontFamily = Recolleta,
                    fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = coffeebeanPurple,
                    titleContentColor = coffeebeanPurple,
                    actionIconContentColor = coffeebeanPurple)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Info Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(text = email, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "$loyaltyPoints pts • $loyaltyTier Member",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { /* Edit Profile */ }) {
                        Text("Edit Profile")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Order History Section
            SectionTitle("Order History")
            Spacer(Modifier.height(8.dp))
            listOf("Cappuccino - ₱180", "Iced Latte - ₱150", "Mocha - ₱190").forEach { order ->
                ListItem(
                    headlineContent = { Text(order) },
                    supportingContent = { Text("View Details") },
                    modifier = Modifier.clickable { /* Navigate to order details */ },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
                )
                Divider()
            }

            Spacer(Modifier.height(24.dp))

            // Payment Methods Section
            SectionTitle("Payment Methods")
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    PaymentMethodRow("Visa •••• 1234")
                    PaymentMethodRow("GCash Account")
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { /* Add payment */ }) {
                        Text("Add New Payment Method")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Settings Section
            SectionTitle("Settings")
            Spacer(Modifier.height(8.dp))
            ListItem(
                headlineContent = { Text("Notifications") },
                modifier = Modifier.clickable { /* Navigate to settings */ }
            )
            Divider()
            ListItem(
                headlineContent = { Text("Privacy Policy") },
                modifier = Modifier.clickable { /* Navigate to privacy */ }
            )
            Divider()
            ListItem(
                headlineContent = { Text("Logout") },
                modifier = Modifier.clickable { /* Logout */ },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    )
}

@Composable
fun PaymentMethodRow(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        IconButton(onClick = { /* Manage method */ }) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Manage")
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun sc() {
    val navController = rememberNavController()
    AccountsScreen(
        navController = navController)
}