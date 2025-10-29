package com.coffeebean.ui.feature.rewards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coffeebean.ui.theme.Recolleta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    navController: NavHostController,
    points: Int = 120,
    nextRewardGoal: Int = 200,
    rewards: List<Reward> = sampleRewards()
) {
    val progress = points.toFloat() / nextRewardGoal.toFloat()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title ={
                    Text(
                        "My Rewards",
                        fontFamily = Recolleta,
                        fontWeight = FontWeight.Bold,
                         )
                    },
                actions = {
                    IconButton(onClick = { /* Navigate to profile */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color(0xFF532D6D),
                    titleContentColor = Color(0xFF532D6D),
                    actionIconContentColor = Color(0xFF532D6D)
                )
            )
        },
        bottomBar = {
            // Optional BottomNavigation if part of larger app
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Current Points Section
            Text(
                text = "You have $points points",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(16.dp))

            // Progress Indicator
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Text(
                text = "${nextRewardGoal - points} points to next reward",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Available Rewards
            Text(
                text = "Available Rewards",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(Modifier.height(8.dp))

            rewards.forEach { reward ->
                RewardCard(reward = reward)
            }

            Spacer(Modifier.height(24.dp))

            // Earn More Points Section
            Text(
                text = "How to Earn More Points",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Buy more drinks, refer a friend, or join promotions to earn extra points!",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun RewardCard(reward: Reward) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(reward.name, style = MaterialTheme.typography.titleMedium)
                Text("${reward.pointsRequired} points", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { /* Redeem action */ }) {
                Text("Redeem")
            }
        }
    }
}

data class Reward(val name: String, val pointsRequired: Int)

fun sampleRewards() = listOf(
    Reward("Free Coffee", 100),
    Reward("50% Off Pastry", 150),
    Reward("Coffee Mug", 300)
)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Sc() {
    val navController = rememberNavController()
    RewardsScreen(
        navController = navController
    )
}
